"""Round-trip check: gzip-read a produced Java .nbt (big-endian) and dump it."""
import sys, gzip, struct

TAG_END,TAG_BYTE,TAG_SHORT,TAG_INT,TAG_LONG,TAG_FLOAT,TAG_DOUBLE=0,1,2,3,4,5,6
TAG_BYTE_ARRAY,TAG_STRING,TAG_LIST,TAG_COMPOUND,TAG_INT_ARRAY,TAG_LONG_ARRAY=7,8,9,10,11,12

def rstr(b,p):
    (n,)=struct.unpack_from(">H",b,p); p+=2
    return b[p:p+n].decode("utf-8"),p+n

def rpay(b,p,t):
    if t==TAG_BYTE:  return struct.unpack_from(">b",b,p)[0],p+1
    if t==TAG_SHORT: return struct.unpack_from(">h",b,p)[0],p+2
    if t==TAG_INT:   return struct.unpack_from(">i",b,p)[0],p+4
    if t==TAG_LONG:  return struct.unpack_from(">q",b,p)[0],p+8
    if t==TAG_FLOAT: return struct.unpack_from(">f",b,p)[0],p+4
    if t==TAG_DOUBLE:return struct.unpack_from(">d",b,p)[0],p+8
    if t==TAG_STRING:return rstr(b,p)
    if t==TAG_BYTE_ARRAY:
        (n,)=struct.unpack_from(">i",b,p);p+=4;return list(struct.unpack_from(">%db"%n,b,p)),p+n
    if t==TAG_LIST:
        it=b[p];(n,)=struct.unpack_from(">i",b,p+1);p+=5;out=[]
        for _ in range(n):
            v,p=rpay(b,p,it);out.append(v)
        return out,p
    if t==TAG_COMPOUND:
        d={}
        while True:
            ct=b[p];p+=1
            if ct==TAG_END:break
            nm,p=rstr(b,p);v,p=rpay(b,p,ct);d[nm]=v
        return d,p
    if t==TAG_INT_ARRAY:
        (n,)=struct.unpack_from(">i",b,p);p+=4;return list(struct.unpack_from(">%di"%n,b,p)),p+4*n
    raise ValueError("tag %d"%t)

def load(path):
    b=gzip.open(path,"rb").read()
    t=b[0];_,p=rstr(b,1);root,p=rpay(b,p,t)
    assert p==len(b),"trailing bytes %d/%d"%(p,len(b))
    return root

for path in sys.argv[1:]:
    r=load(path)
    print("="*70)
    print("FILE:",path)
    print("DataVersion:",r["DataVersion"],"  size:",r["size"],"  blocks:",len(r["blocks"]))
    print("palette (%d entries):"%len(r["palette"]))
    for i,e in enumerate(r["palette"]):
        print("  [%2d] %s %s"%(i,e["Name"],e.get("Properties","")))
    # show a few interesting blocks: jigsaw + waterlogged + stairs
    shown_j=shown_w=shown_s=0
    for blk in r["blocks"]:
        e=r["palette"][blk["state"]]; nm=e["Name"]; props=e.get("Properties",{})
        if "nbt" in blk and blk["nbt"].get("id")=="minecraft:jigsaw" and shown_j<3:
            print("  JIGSAW pos",blk["pos"],"orient=",props.get("orientation"),"->",dict(blk["nbt"]));shown_j+=1
        elif props.get("waterlogged")=="true" and shown_w<3:
            print("  WATERLOGGED pos",blk["pos"],nm,dict(props));shown_w+=1
        elif nm.endswith("stairs") and shown_s<2:
            print("  STAIRS pos",blk["pos"],nm,dict(props));shown_s+=1
