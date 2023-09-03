# Define the input file and output file names
input_file = "U:/ExtraBiomes/codebase/Extrabiomes - Forge/src/main/resources/assets/extrabiomes/lang/en_us.lang"
output_file = "U:/ExtraBiomes/codebase/Extrabiomes - Forge/src/main/resources/assets/extrabiomes/lang/en_uk.json"

# Open the input and output files
with open(input_file, "r") as infile, open(output_file, "w") as outfile:
    # Iterate through each line in the input file
    for line in infile:
        if line != "\n":
            # Replace ":" with ".", "=" with '": "', and wrap the line in double quotation marks
            modified_line = '"' + line.replace(":", ".").replace("=", '": "') + '",'
        
            # Write the modified line to the output file
            outfile.write(modified_line)

print(f"Conversion completed. The result has been saved to '{output_file}'.")
