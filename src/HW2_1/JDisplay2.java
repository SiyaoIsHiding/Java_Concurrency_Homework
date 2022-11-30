package HW2_1;

import java.util.Date;

public class JDisplay2 implements HighLevelDisplay {

    private final JDisplay d = new JDisplay();
    private String[] text = new String[100];
	private volatile int usedRows;

	private final Date lock = new Date();

    public JDisplay2(){
	clear();
    }

    private synchronized void updateRow(int row, String str) {
		synchronized (text){
			text[row] = str;
			if (row < d.getRows()) {
				for(int i=0; i < str.length(); i++)
					d.write(row,i,str.charAt(i));
				for(int i=str.length(); i < d.getCols(); i++)
					d.write(row,i,' ');
			}
		}
    }

    private synchronized void flashRow(int row, int millisecs) {
		synchronized (lock){
			String txt = text[row];
			try {
				for (int i= 0; i * 200 < millisecs; i++) {
					updateRow(row,"");
					Thread.sleep(70);
					updateRow(row,txt);
					Thread.sleep(130);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

    }

    public synchronized void clear() {
	for(int i=0; i < d.getRows(); i++)
	    updateRow(i,"");
	usedRows = 0;
    }

    public synchronized void addRow(String str) {
		System.out.println("add Row: "+str);
		synchronized (text){
			updateRow(usedRows,str);
			usedRows++;
			flashRow(usedRows-1,1000);

		}

    }

    public synchronized void deleteRow(int row) {
		System.out.println("delte row: "+ row);
		synchronized (text){
			if (row < usedRows) {
				for(int i = row+1; i < usedRows; i++)
					updateRow(i-1, text[i]);
				usedRows--;
				updateRow(usedRows,"");
				if(usedRows >= d.getRows()) flashRow(d.getRows()-1,1000);
			}
		}

    }


}