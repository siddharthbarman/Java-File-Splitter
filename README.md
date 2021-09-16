# Java-File-Splitter
Splits a file into multiple chunks, joins the chunks back to form the original file

# Syntax
splitter -mode -n size_in_bytes -i filepath -o filepath

# Split example
splitter -s -n 1024 -i example.zip -o ./output/splitted
The example will split the input file example.zip into multiple files of 1 KB size.
The split files will be stored in a folder named output, the split files will be named
splitted.001, splitted.002, spitted.003 and so on.

# Join example
splitter -j -i splitted.001 -o ./output/joined.zip
The example will join all the files starting with 001 and create a new file named joined.zip in a folder named output

# Limitation
Minimum size of a chunk is 4096. Chunk size need to be a multiple of 4096. 
Maximum number of chunks is 999, the program will error out if the number of chunks needed is more than 999.
