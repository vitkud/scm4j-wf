package org.scm4j.actions;

import java.io.PrintStream;

import com.google.common.base.Strings;


public class PrintAction {
	
	private int level = 0;
	
	public void print(PrintStream ps, IAction rootAction){
		ps.println(Strings.repeat("\t", level) + rootAction.toString());
		level++;
		for (IAction currentAction : rootAction.getChildActions()) {
			print(ps, currentAction);
		}
		level--;
		
	}
}
