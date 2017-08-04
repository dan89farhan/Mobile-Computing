
package Client;

import java.io.Serializable;

public class ChipCode implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7514449733292903965L;
	public int chipCode[] = {1, 1};
    public String status = "";
    public String message = "";
    public String to = "";
    
    public String toString(){
        return status+" "+message+" "+to+" "+chipCode[0]+" "+chipCode[1];
    }
}
