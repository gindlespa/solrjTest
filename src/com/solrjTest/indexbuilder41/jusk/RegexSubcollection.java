package indexbuilder41.jusk;
import indexbuilder41.utils.DB;
import java.util.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author murphy john
 */
public class RegexSubcollection {
     /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
   //try{
        int maxKeid=0;
        DB db = new DB();
        java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://cw-warehouse3:1433/baseline_depuypinnacle" ,"aspconnect","aspconnect");
        Statement stm = conn.createStatement();
        stm.setQueryTimeout(500);
        ResultSet rs;
        int rssize=0;
        java.util.ArrayList<RegexItem> al = new ArrayList();
        do{

             rs = stm.executeQuery("Select distinct top 1000 keid from portfolioitem  where portfolioid in ('5b6b6885-ce72-45ff-9c4c-5fb09d9c76ff','9dfb924d-78ab-4ad4-b68d-6762abf894cc') and keid > "+maxKeid);
             rssize = 0;
             al = new ArrayList<>();
             while(rs.next()){
                RegexItem ri = new RegexItem();
                ri.ke_id = rs.getInt("keid");
                al.add(ri);
                rssize++;
             }
             rs.close();
             for(RegexItem ri:al){
                String text = "";
                rs = stm.executeQuery("Select doc_number from bates_check_new b where b.ke_id = " + ri.ke_id );
                if(rs.next())
                    text = rs.getString("doc_number");
                Pattern pw = Pattern.compile("\"[^\"]*\"");
                Matcher mat= pw.matcher(text);
                int current = 0;
                while(mat.find(current)){
                    if(mat.group().length()>4 && mat.group().length()<101)
                        System.out.println(ri.ke_id + "\t" + mat.group().replace("\r", "").replace("\n", ""));
                    current = mat.end()+1;
                    
                    
                }
                if(ri.ke_id>maxKeid)
                        maxKeid = ri.ke_id;
             }
             
             
         }
         while(rssize > 0000);

         
    }
    
};

