/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;


// Blob.java
// Andrew Davison, October 2005, ad@fivedots.coe.psu.ac.th

/* A blob is a collecion of (x,y) points with the same
   (or similar) colour which are close to each other. 
*/

import java.awt.*;
import java.util.*;


public class Blob
{
  // no. of points necessary to make a large blob
  private static final int LARGE_BLOB_SIZE = 500; //800

  private static final int PROXIMITY = 4; //4
  /* Used to judge if a new point is close enough to the blob to
     join it. */

  private ArrayList points;   // of Point objects
  private int numPoints;
  private int xSum, ySum;   // sum of (x,y) coords


  public Blob()
  {  points = new ArrayList();
     numPoints = 0;  
     xSum = 0; ySum = 0;
  } // end of Blob()


  public boolean isClose(int x, int y)
  /* is (x,y) close to a point in the blob? */
  {
    Point p;

    for(int i=0; i < numPoints; i++) {
      p = (Point) points.get(i);

      if ((Math.abs(x - p.x) < PROXIMITY) &&
          (Math.abs(y - p.y) < PROXIMITY))
        return true;
    }
    return false;
  }  // end of isClose()


  public boolean addPoint(int x, int y)
  /* add (x,y) to the blob's points, and report if the
     blob is now 'large' */
  {  
    points.add( new Point(x,y) ); 
    numPoints++;
    xSum = x; ySum += y;

    return (numPoints > LARGE_BLOB_SIZE);
  }  // end of addPoint()


  public int numPoints()
  {  return numPoints;  }


  public Point getCenter()
  /* Return the center point of the blob. This is
     not particularly accurate unless the blob is
     convex.
  */
  {
    int xAvg = xSum / numPoints;
    int yAvg = ySum / numPoints;

    return new Point(xAvg, yAvg);
  }  // end of getCenter()


}  // end of Blob class
