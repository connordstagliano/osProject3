# osProject3 Dev Log
## 12/6/24 11:13am START
  This is the first devlog, I am beginning the project for the first time. I expect to use most of today and tomorrow, and possibily the morning of sunday the 8th to complete work.
  The apporach I am planning is to create the menu functionality first, and implement features in order of dependancies. That is, I will implment the create, open, and quit features
  first, as they are necessary for completion of the other features. I will then implement insert and print, to ensure the index file functions correctly, and then finally i will
  implement any other outstanding features. The main goal of this first session is to create a rough plan and gain a basic understanding of the project and idx file functionalities.

## 12/6/2024 1:22pm FIRST MAJOR COMMITT
  First commit made. Program successfully has menu functionality, creates/overwrites files upon request, and can open an existing idx file. Next is to add the creation of file headers, and
  then I will begin to implement the B tree operations.

## 12/6/2024 2:09pm FIRST SESSION COMPLETE
  I have accomplished my goal for the first session of creating the menu functionality as well as the basic operations to get around and use the menu in a way that will allow me to test
  each feature. I also now have a better idea of how I want ot approach the project going forward.

## 12/7/2024 12:08pm SECOND SESSION BEGIN
  Beginning my second session. I committed some small changes I made before I got off last session. I am planning on finishing the project today, as writing and accessing the file has been
  a bit smoother than I initially anticipated. If I am able to get the insertions into the B-Tree correct, then I don't expect there to be too many other difficulties.

## 12/7/2024 1:59pm SECOND MAJOR COMMIT
  Compelted insertion functionality. It is very difficult to test and ensure that the nodes are correctly inserted into the tree, but based on what I ahve seen, it appears to work mostly correctly.
  Now it is time to implement the rest of the "minor" features, which will complete the functionality of the program. I also need to not forget to add a little bit of input validiation.

## 12/7/2024 3:08pm MAJOR BUG ENCOUNTER
  Encountered a bug when writing the search function that causes the file to be read incorrectly. Thinking that it has something to do with the calculated
  offset during each recruive search functionc call.

### 12/7/2024 3:28pm MAJOR BUGFIX
  Fixed previous bug by changing offset from blockID * 512 to (blockID - 1) * 512. Changes have been committed, and this completes the search functionality.

## 12/7/2024 5:55pm SESSION 3
  Took a break, will attempt to finish project this session. Just need to implement load and extract features, as well as finish some input validation
  checks.
  
## 12/7/2024 7:25pm FINAL COMMIT
  Adding my final commit after this entry. Added both load and extract functionalty, and implemented input validation everywhere I could find that needed it. As far as final thoughts about my program:
  I only created two classes, the Menu and the IndexManger, where the latter contains all functionalties relating to the index file. If I were to do this again, I would take more time at the beginning
  to divide up the methods further into more specialized classes, which would have allowed for easier debugging. There are only two bugs that I know about, one major and one minor. The major bug refers
  to the extract method only extracting data from the final block. I feel the solution to this lies once again the in offset calculations for reading each pair from the file, but I was unsuccessful in
  correcting the error. The minor bug refers to printing an empty index, which correctly prints the header of the file, but then prints a ghost block one with a garbage block id. I feel as 90% of the
  functionalties work correctly.
