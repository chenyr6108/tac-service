package com.brick.bpm.util;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.Expression;
import org.apache.taglibs.standard.lang.jstl.ExpressionString;
import org.apache.taglibs.standard.lang.jstl.VariableResolver;
import org.apache.taglibs.standard.lang.jstl.parser.ELParser;
import org.apache.taglibs.standard.lang.jstl.parser.ParseException;

public class ElUtil {
	
	public class MapVariableResolver implements VariableResolver {
		
		private Map variableMap;
		
		public MapVariableResolver() {
		    this.variableMap = new HashMap();
		}
		
		public MapVariableResolver(Map variableMap) {
		    this.variableMap = variableMap;
		}
		
		public void addVariable(String variable, Object value) {
		    this.variableMap.put(variable, value);
		}

		@Override
		public Object resolveVariable(String pName, Object arg1)
				throws org.apache.taglibs.standard.lang.jstl.ELException {
			return this.variableMap.get(pName);
		}
	}
	
	private Map fnMap;
	
	public ElUtil() {
		fnMap = new HashMap();
		for (Method method : ElFnSupport.class.getDeclaredMethods()) {
			//System.out.println(method.getName());
			fnMap.put("fn:" + method.getName(), method);
		}
	}
	
	public String evaluate(Map data, String input) {
        try {
        	MapVariableResolver resolver = new MapVariableResolver(data);
            StringReader rdr = new StringReader(input);
            ELParser parser = new ELParser(rdr);
            Object result = parser.ExpressionString();
            if(result instanceof String) {
                return (String) result;
            } else if(result instanceof Expression) {
                Expression expr = (Expression) result;
                result = expr.evaluate(null, resolver, fnMap, "fn", null);
                return result == null ? null : result.toString();
            } else if(result instanceof ExpressionString) {
                Expression expr = (Expression) result;
                result = expr.evaluate(null, resolver, fnMap, "fn", null);
                return result == null ? null : result.toString();
            } else {
                throw new RuntimeException("Incorrect type returned; not String, Expression or ExpressionString");
            }
        } catch(ParseException pe) {
            throw new RuntimeException("ParseException thrown: " + pe.getMessage(), pe);
        } catch(ELException ele) {
            throw new RuntimeException("ELException thrown: " + ele.getMessage(), ele);
        }
    }

}
