# HDLF-Display-System
This is a repository that provides access to the source code and material used to implement an High Density and Large Format (HDLF) display system that was created as a part of my master's thesis

This repository contains the source code of the both client side and server side of the HDLF data visualization system that i implemented. It also contains the link to the tools and libraries used for the work.

1) Server Side Components<br />
  a) HTTP Data Server: Server that streams data required for visualization. Available in "Server Side Components/Data Server" directory.<br />
  b) Spark Application: PySpark application that creates simulation data using Apache Spark. Available in "Server Side Components/Spark Application" directory.<br />
  
2) Client Side Components<br />
  a) PsExec : Command line tool that performs remote desktop management used for launching views in all 9 monitors. Available in PsTools.zip.<br />
  b) View Initializer: Java application that opens a visualization in all 9 monitors. Available in "Client Side Components/View Initializer" directory.<br />
  c) WebSocket Server: Server written in java that communicates with browser clients for view synchronization. Available in "Client Side Components/WebSocket Server" directory.<br />
  d) D3 Visualization Pages: Files containing visualization code written in D3.js for animating global wind flow simulation on HDLF Display. Available in "Client Side Components/D3 Visualization Pages.<br />
  e) Browser Launch Files: Batch script for opening D3 page in a Google Chrome process. Available in "BrowserLaunchFiles"<br />
