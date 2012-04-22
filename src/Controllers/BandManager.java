/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
	
// BandManager.java
// Andrew Davison, October 2005, ad@fivedots.coe.psu.ac.th

/* Manages all the blobs created for a given band colour.

   BandManager offers isBandColour() to check if a pixel
   is close to the band manager's colour.

   addPoint() adds a pixel coordinate to a blob, and reports
   if a large enough blob has been made.
*/
package Controllers;

import java.awt.*;
import java.util.*;


public class BandManager
{
  // max distance*distance to the band colour
  private static final int MAX_DIST2 = 5000;	//5000


  private String bandName;

  // RGB components for this band manager's colour
  private int redCol, greenCol, blueCol;

  private ArrayList blobs;   // of Blob objects
  private int currBlobIdx;  // index of last blob that was updated
  

  public BandManager(String nm, Color c)
  {
    bandName = nm;

    redCol = c.getRed();
    greenCol = c.getGreen();
    blueCol = c.getBlue();
  /*  System.out.println("redCol = " + redCol);
    System.out.println("greenCol = " + greenCol);
    System.out.println("blueCol = " + blueCol);
    System.out.println("");
    System.out.println("");
    
    redCol = 0xBE0032;
    greenCol = 0x007959;
    blueCol = 0xB9B459;
*/
    
    blobs = new ArrayList();
    currBlobIdx = -1;
  }  // end of BandManager()


  public void clear()
  // delete any blobs stored here
  {  blobs.clear();  
     currBlobIdx = -1;
  }


  public boolean isBandColour(int r, int g, int b)
  /* is (r,g,b) close enough to the band colour? */
  {
/*
	    System.out.println("\n\nRed: " + r);
	    System.out.println("green: " + g);
	    System.out.println("blue: " + b);

	    System.out.println("\nRedCol: " + redCol);
	    System.out.println("greenCol: " + greenCol);
	    System.out.println("blueCol: " + blueCol);
	    */
    int redDiff = r - redCol;
    int greenDiff = g - greenCol;
    int blueDiff = b - blueCol;

    
    return (((redDiff * redDiff) + 
             (greenDiff * greenDiff) + 
             (blueDiff * blueDiff)) < MAX_DIST2);
  }  // end of isBlobColour()



  public int addPoint(int x, int y)
  /* Add (x,y) to an existing blob if close enough, or create
     a new blob for it. Return the blob index if the blob is now
     big enough, otherwise return -1.
  */
  {  
    int largeIdx = -1;   // index of Blob with enough points

    int blobIndex = findCloseBlob(x,y);
    if (blobIndex != -1) {   // found a blob close to (x,y)
      Blob b = (Blob) blobs.get(blobIndex);
      boolean isLarge = b.addPoint(x,y);
      currBlobIdx = blobIndex;
      if (isLarge)   // created a large enough blob
        largeIdx = blobIndex;
    }
    else {   // no close blob, so create a new one
      Blob b = new Blob();
      b.addPoint(x,y);
      blobs.add(b);
      currBlobIdx = blobs.size() - 1;
    }
    return largeIdx;
  }  // end of addPoint()


  private int findCloseBlob(int x, int y)
  /* find a blob that's close to (x,y) */
  {
    Blob blob;

    // try current blob first
    if (currBlobIdx != -1) {
      blob = (Blob) blobs.get(currBlobIdx);
      if (blob.isClose(x,y))
        return currBlobIdx;
    }

    // otherwise try the others
    for(int i=0; i < blobs.size(); i++) { 
      if (i != currBlobIdx) {
        blob = (Blob) blobs.get(i);
        if (blob.isClose(x,y))
          return i;
      }
    }
    return -1;   // didn't find a close blob
  }  // end of findCloseBlob()
 


  public String getName()
  {  return bandName;  }


  public Point getBlobCenter(int idx)
  // return the center point for the blob at position idx
  {
    Point p = null;
    if ((idx < 0) || (idx >= blobs.size()))
      System.out.println("No blob with that index: " + idx);
    else {
      Blob blob = (Blob) blobs.get(idx);
      p = blob.getCenter();
    }
    return p;
  }  // end of getBlobCenter()


}  // end of BandManager class