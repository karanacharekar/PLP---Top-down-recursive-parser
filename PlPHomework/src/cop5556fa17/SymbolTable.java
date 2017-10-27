package cop5556fa17;

import java.util.HashMap;
import java.util.Stack;

import cop5556fa17.AST.Declaration;


public class SymbolTable {
	
	
	HashMap<String,HashMap<Integer,Declaration>> hm;	
	Stack<Integer> st;
	int currscope , nextscope;
	
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		hm = new HashMap<String,HashMap<Integer,Declaration>>();	
		st = new Stack<Integer>();
		nextscope = 1;
		currscope = 0;
		st.push(currscope++);
		
	}


	@Override
	public String toString() {
		return "";
	}	
	
	
	public void enterScope(){
		currscope = nextscope++;
		st.push(currscope);
		
	}
	

	public void leaveScope(){
			st.pop();
	}

	
	public boolean insert(String ident, Declaration dec){
		//TODO:  IMPLEMENT THIS
		
		HashMap<Integer,Declaration>temp = null;
		
		if(!hm.containsKey(ident))
		{
			temp = new HashMap<Integer,Declaration>();
			temp.put(st.peek(),dec);
			this.hm.put(ident,temp);
		}
		else		
		{
			temp = hm.get(ident);
			if(temp.containsKey(st.peek()))
				return false;
			else
			{
				temp.put(st.peek(),dec);
				//this.hm.put(ident,temp);
				return true;
			}
		}
		
		return true;
	}
	
	
	public Declaration lookup(String ident){
		//TODO:  IMPLEMENT THIS
		Declaration d;
		HashMap<Integer,Declaration> temp = null;
		int tempscope = st.peek();
		
		if(hm.containsKey(ident))
		{
			temp = hm.get(ident);
		}

		if(temp == null)
			return null;
		else
		{
			d = null;
	       for(int i = st.size() - 1 ; i>=0 ;i--)
	       {
			d = temp.get(st.get(i));
			if(d!= null)
				break;
	       }
		}
				
		return d;
	}
	
}
