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
