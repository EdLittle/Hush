/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.util.LinkedList;

import java.util.Stack;

/**
 *
 * @author FREAK
 */
public class RoutesManager {
    private static Stack<String> navigationStack = new Stack<String>();
    
    public static void push(String card){
        System.out.println("Pushing " + card);
        navigationStack.push(card);    
    }
    
    public static String pop(){
        if(navigationStack.peek() != null){
            System.out.println("Popping " + navigationStack.peek());
            return navigationStack.pop();
        }
        else{
            return null;
        }
    }
}
