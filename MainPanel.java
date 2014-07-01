import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import oracle.jdbc.driver.OracleResultSet;
import oracle.sql.Datum;
import oracle.sql.STRUCT;

public class MainPanel extends javax.swing.JPanel {

    int mouseX=0,mouseY=0;
    String arg="";
    int pointQuery=0;
    static int queryCount=0;
    int noofpoints=0,lastx=-1,lasty=-1,firstx=-1,firsty=-1;
    database db=new database();
    String EQquery="",IDEQquery="";
    int regionClosed=0;
    Connection conn=db.getDBConnection();
    public MainPanel() {
        initComponents();
        db.getDBConnection();
    }

    private void paintComponentStudent(Graphics g,Color c,String query){
        try {
            
            OracleResultSet rs=(OracleResultSet)db.getQueryResult(query);
            g.setColor(c);
            while(rs.next()){
            STRUCT struct=(STRUCT)rs.getObject(1);
            Datum[] dat=((STRUCT)struct.getAttributes()[2]).getOracleAttributes();
            g.drawRect(dat[0].intValue()-5, dat[1].intValue()-5,10,10);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void paintComponentStudent_SS(Graphics g,Color c,String query,int queryCount){
        try {
            g.setColor(c);
            query="select location from student s where mdsys.sdo_relate(s.location, ("+query+"),'mask=INSIDE+INTERSECT') = 'TRUE'";  
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentStudent(imagePanel.getGraphics(),Color.green,query);
        } catch (Exception ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void paintComponentStudent_AS_EQ(Graphics g,String query,String idquery,int queryCount){
        try {
            String IgnoreID="";
            Graphics2D g2d=(Graphics2D)g;
            Color[] color={Color.GRAY,Color.YELLOW,Color.PINK,Color.WHITE,Color.ORANGE,Color.CYAN,Color.BLUE,Color.MAGENTA,new Color(245,185,3),new Color(128,128,64)};
            ArrayList al=new ArrayList();            
            OracleResultSet rs=(OracleResultSet)db.getQueryResult(idquery);
            rs.next();
            IgnoreID=rs.getString(1);
            al.add(IgnoreID);
            query="select location from student s where mdsys.sdo_relate(s.location, ("+query+"),'mask=INSIDE+INTERSECT') = 'TRUE'";  
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            rs=db.getQueryResult(query);
            while(rs.next()){ 
            STRUCT struct=(STRUCT)rs.getObject(1);
            Datum[] dat=((STRUCT)struct.getAttributes()[2]).getOracleAttributes();
            String arg="";
            arg=dat[0].intValue()+","+dat[1].intValue();
            query="select shape,id from announcement_system s where mdsys.sdo_nn(s.shape, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+arg+",null),null,null),'sdo_num_res=2') = 'TRUE'";
            OracleResultSet tmprs=(OracleResultSet)db.getQueryResult(query);
            int colorIndex=-1;
            while(tmprs.next()){
            if(!tmprs.getString(2).equals(IgnoreID)){
                if(al.contains(tmprs.getString(2)))
                    colorIndex=al.indexOf(tmprs.getString(2));
                else
                {
                    al.add(tmprs.getString(2));
                    colorIndex=al.indexOf(tmprs.getString(2));
                    STRUCT struct1=(STRUCT)tmprs.getObject(1);
                    paintComponentAnnouncementSystem(g, color[colorIndex], struct1);
                }
            }
            }
            g2d.setColor(color[colorIndex]);
            Rectangle2D r2d = new Rectangle2D.Float(dat[0].floatValue()-2.5f, dat[1].floatValue()-2.5f,5f,5f);
            g2d.draw(r2d);
            }
            queryTextList.append("Query "+queryCount+". "+query+"\n");
        } catch (SQLException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void paintComponentBuilding(Graphics g,Color c,String query){
        try {
            OracleResultSet rs=(OracleResultSet)db.getQueryResult(query);
            g.setColor(c);
            while(rs.next()){
            STRUCT struct=(STRUCT)rs.getObject(1);
            oracle.sql.ARRAY arr= (oracle.sql.ARRAY) struct.getAttributes()[4];
            Datum[] dat=arr.getOracleArray();
            for(int i=0;i<dat.length-2;i+=2)
            {
                g.drawLine(dat[i].intValue(), dat[i+1].intValue(),dat[i+2].intValue(),dat[i+3].intValue());
            }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void paintComponentAnnouncementSystem(Graphics g,Color c,STRUCT struct){
        try {
            g.setColor(c);
            oracle.sql.ARRAY arr= (oracle.sql.ARRAY) struct.getAttributes()[4];
            Datum[] dat=arr.getOracleArray();
            int radius=dat[2].intValue()-dat[0].intValue();
            g.drawOval(dat[0].intValue()-radius,dat[3].intValue()-radius,2*radius,2*radius);
            int width=15;
            g.drawRect(dat[0].intValue()-(width/2),dat[3].intValue()-(width/2),width,width);
        } catch (SQLException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void clear()
    {
        mouseX=-1;
        mouseY=-1;
        arg="";
        noofpoints=0;
        lastx=-1;
        lasty=-1;
        firstx=-1;
        firsty=-1;
        pointQuery=0;
        regionClosed=0;
    }
    private void paintComponentAnnouncementSystem(Graphics g,Color c,String query){
        try {
            OracleResultSet rs=(OracleResultSet)db.getQueryResult(query);
            g.setColor(c);
            while(rs.next()){
            STRUCT struct=(STRUCT)rs.getObject(1);
            oracle.sql.ARRAY arr= (oracle.sql.ARRAY) struct.getAttributes()[4];
            Datum[] dat=arr.getOracleArray();
            int radius=dat[2].intValue()-dat[0].intValue();
            g.drawOval(dat[0].intValue()-radius,dat[3].intValue()-radius,2*radius,2*radius);
            int width=15;
            g.drawRect(dat[0].intValue()-(width/2),dat[3].intValue()-(width/2),width,width);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void paintComponentPoint(Graphics g,Color c,int x,int y)
    {  
            int radius=50;
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(c);
            Rectangle2D r2d = new Rectangle2D.Float(x-2.5f, y-2.5f,5f,5f);
            g2d.draw(r2d);
            g.drawOval(x-radius,y-radius,2*radius,2*radius);
    }
    private void paintComponentSquarePoint(Graphics g,Color c,int x,int y)
    {  
            Graphics2D g2d = (Graphics2D) g;
            g.setColor(c);
            Rectangle2D r2d = new Rectangle2D.Float(x-2.5f, y-2.5f,5f,5f);
            g2d.draw(r2d);
    }

    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        cbAS = new javax.swing.JCheckBox();
        cbB = new javax.swing.JCheckBox();
        cbS = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        rbWR = new javax.swing.JRadioButton();
        rbRQ = new javax.swing.JRadioButton();
        rbPQ = new javax.swing.JRadioButton();
        rbEQ = new javax.swing.JRadioButton();
        rbSS = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        imagePanel = new javax.swing.JPanel();
        imageLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        xTxt = new javax.swing.JTextField();
        yTxt = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        queryTextList = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(1024, 760));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Active Feature Types"), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(153, 153, 153)));

        cbAS.setText("Announcement System");
        cbAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbASActionPerformed(evt);
            }
        });

        cbB.setText("Building");
        cbB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBActionPerformed(evt);
            }
        });

        cbS.setText("Student");
        cbS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbS)
                    .addComponent(cbB)
                    .addComponent(cbAS))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(cbAS)
                .addGap(3, 3, 3)
                .addComponent(cbB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbS)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Query"));

        buttonGroup1.add(rbWR);
        rbWR.setText("Whole Region");
        rbWR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbWRActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbRQ);
        rbRQ.setText("Range Query");
        rbRQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbRQActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbPQ);
        rbPQ.setText("Point Query");
        rbPQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPQActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbEQ);
        rbEQ.setText("Emergency Query");
        rbEQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbEQActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbSS);
        rbSS.setText("Surrounding Student");
        rbSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbWR)
                    .addComponent(rbSS)
                    .addComponent(rbRQ)
                    .addComponent(rbPQ)
                    .addComponent(rbEQ))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbWR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbPQ)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbRQ)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbSS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbEQ)
                .addContainerGap(131, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Map View")));
        jPanel4.setPreferredSize(new java.awt.Dimension(1024, 661));

        imagePanel.setMaximumSize(new java.awt.Dimension(820, 580));
        imagePanel.setMinimumSize(new java.awt.Dimension(820, 580));
        imagePanel.setPreferredSize(new java.awt.Dimension(820, 580));
		try{
        imageLabel.setIcon(new javax.swing.ImageIcon(ImageIO.read(new File("map.jpg")))); // NOI18N
		}
		catch(Exception e){
		System.out.println(e);
		}
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageLabelMouseClicked(evt);
            }
        });
        imageLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                imageLabelMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel2.setText("X:");

        jLabel3.setText("Y:");

        xTxt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xTxt.setEnabled(false);

        yTxt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        yTxt.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1056, 1056, 1056))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(xTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)))
        );

        jButton1.setText("Submit Query");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitQuery(evt);
            }
        });

        queryTextList.setColumns(20);
        queryTextList.setRows(5);
        jScrollPane2.setViewportView(queryTextList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 633, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void imageLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLabelMouseMoved
        xTxt.setText(""+evt.getX());
        yTxt.setText(""+evt.getY());
    }//GEN-LAST:event_imageLabelMouseMoved
    
    private void imageLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLabelMouseClicked
        if(rbPQ.isSelected())
        {
            clear();
            mouseX=evt.getX();
            mouseY=evt.getY();
            pointQuery=1;
            paintComponentPoint(imagePanel.getGraphics(),Color.red,mouseX,mouseY);
        }
        if(rbRQ.isSelected())
        {
            if(evt.getButton() == MouseEvent.BUTTON3)
            {
                if(lastx<0 && lasty <0)
                {
                    JOptionPane.showMessageDialog(null,"First select the points");
                }
                else if(noofpoints < 3 )
                    JOptionPane.showMessageDialog(null,"Please select atleast three points");
                else
                {
                    regionClosed=1;
                    Graphics g=imagePanel.getGraphics();
                    g.setColor(Color.red);
                    g.drawLine(lastx,lasty,firstx,firsty);
                    arg+=firstx+","+firsty;
                }    
            }
            if(evt.getButton() == MouseEvent.BUTTON1)
            {
                noofpoints++;
                if(lastx<0 && lasty <0)
                {
                    firstx=evt.getX();
                    firsty=evt.getY();
                    lastx=evt.getX();
                    lasty=evt.getY();
                    arg+=firstx+","+firsty+",";
                }
                else
                {
                    Graphics g=imagePanel.getGraphics();
                    g.setColor(Color.red);
                    g.drawLine(lastx,lasty,evt.getX(),evt.getY());
                    lastx=evt.getX();
                    lasty=evt.getY();
                    arg+=lastx+","+lasty+",";
                }    
            }
        }
        if(rbSS.isSelected())
            {
                
                clear();
                pointQuery=1;
                mouseX=evt.getX();
                mouseY=evt.getY();
                paintComponentSquarePoint(imagePanel.getGraphics(),Color.red,mouseX,mouseY);
                String arg=mouseX+","+mouseY;
                String query="select shape from announcement_system s where mdsys.sdo_nn(s.center, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+arg+",null),null,null),'sdo_num_res=1') = 'TRUE'";
                queryTextList.append("Query "+queryCount+". "+query+"\n");
                paintComponentAnnouncementSystem(imagePanel.getGraphics(), Color.red, query);
            }
        if(rbEQ.isSelected())
            {
                clear();
                pointQuery=1;
                mouseX=evt.getX();
                mouseY=evt.getY();
                paintComponentSquarePoint(imagePanel.getGraphics(),Color.red,mouseX,mouseY);
                String tmp=mouseX+","+mouseY;
                String query="select id from announcement_system s where mdsys.sdo_nn(s.shape, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+tmp+",null),null,null),'sdo_num_res=1') = 'TRUE'";
                IDEQquery=query;
                query="select shape from announcement_system s where mdsys.sdo_nn(s.shape, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+tmp+",null),null,null),'sdo_num_res=1') = 'TRUE'";
                EQquery=query;
                paintComponentAnnouncementSystem(imagePanel.getGraphics(),Color.red,query);
            }
    }//GEN-LAST:event_imageLabelMouseClicked

    private void submitQuery(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitQuery
        if(!rbWR.isSelected() && !rbPQ.isSelected() && !rbRQ.isSelected() && !rbSS.isSelected() && !rbEQ.isSelected())
            JOptionPane.showMessageDialog(null,"Please select query");
        else{
            queryCount++;
        if(rbWR.isSelected())
        {
            if(!cbAS.isSelected() && !cbB.isSelected() && !cbS.isSelected())
                JOptionPane.showMessageDialog(null,"Please select feature");
            else{
            if(cbAS.isSelected()){
            String query= "select shape from announcement_system";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentAnnouncementSystem(imagePanel.getGraphics(),Color.red,query);
            }
            if(cbB.isSelected()){
            String query= "select shape from building";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentBuilding(imagePanel.getGraphics(),Color.yellow,query);
            }
            if(cbS.isSelected())
            {
            String query="select location from student";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentStudent(imagePanel.getGraphics(),Color.green,query);
            }
            }
        }
        if(rbPQ.isSelected())
        {
            if(pointQuery==0)
            { 
                JOptionPane.showMessageDialog(null,"Please select a point on Map");
            }
            else{
                paintComponentPoint(imagePanel.getGraphics(),Color.red,mouseX,mouseY);
            if(!cbAS.isSelected() && !cbB.isSelected() && !cbS.isSelected())
                JOptionPane.showMessageDialog(null,"Please select feature");
            else{
            int r=50,x=mouseX,y=mouseY;
            String arg=""+x+","+(y+r)+","+(x+r)+","+y+","+x+","+(y-r);
            if(cbAS.isSelected())
            {
            
            String query="select shape,id from announcement_system s where s.id in (select id from announcement_system s where mdsys.sdo_relate(s.shape, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,4),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=ANYINTERACT') = 'TRUE') and mdsys.sdo_nn(s.shape, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+mouseX+","+mouseY+",null),null,null),'sdo_num_res=1') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentAnnouncementSystem(imagePanel.getGraphics(),Color.yellow,query);
            
            String nearestID=db.getID(query);
            
            query="select shape from announcement_system s where id not like '"+nearestID+"' and mdsys.sdo_relate(s.shape, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,4),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=ANYINTERACT') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentAnnouncementSystem(imagePanel.getGraphics(),Color.green,query);
            }
            if(cbB.isSelected())
            {
            String query="select shape,id from building s where s.id in (select id from building b where mdsys.sdo_relate(b.shape, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,4),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=ANYINTERACT') = 'TRUE') and mdsys.sdo_nn(s.shape, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+mouseX+","+mouseY+",null),null,null),'sdo_num_res=1') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentBuilding(imagePanel.getGraphics(),Color.yellow,query);
            
            String nearestID=db.getID(query);
            
            query="select shape from building b where id not like '"+nearestID+"' and mdsys.sdo_relate(b.shape, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,4),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=ANYINTERACT') = 'TRUE'";    
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentBuilding(imagePanel.getGraphics(),Color.green,query);
            }if(cbS.isSelected())
            {
            String query="select location,id from student s where s.id in (select id from student s where mdsys.sdo_relate(s.location, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,4),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=INSIDE+INTERSECT') = 'TRUE') and mdsys.sdo_nn(s.location, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+mouseX+","+mouseY+",null),null,null),'sdo_num_res=1') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentStudent(imagePanel.getGraphics(),Color.yellow,query);
            
            String nearestID=db.getID(query);
            query="select location from student s where id not like '"+nearestID+"' and  mdsys.sdo_relate(s.location, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,4),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=INSIDE+INTERSECT') = 'TRUE'";    
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentStudent(imagePanel.getGraphics(),Color.green,query);
            }
            }
            }
        }
        if(rbRQ.isSelected())
        {
            if(!cbAS.isSelected() && !cbB.isSelected() && !cbS.isSelected())
                JOptionPane.showMessageDialog(null,"Please select feature");
            else{
            if(noofpoints<3)
            {   
                clear();
                imagePanel.repaint();
                JOptionPane.showMessageDialog(null,"Please select a region on Map");
            }
            else if(regionClosed==0)
            {
                clear();
                imagePanel.repaint();
                JOptionPane.showMessageDialog(null,"You din't close the region properly. \n Please select region again.");
            }    
            else{
            if(cbAS.isSelected()){
            String query="select shape from announcement_system s where mdsys.sdo_relate(s.shape, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=ANYINTERACT') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentAnnouncementSystem(imagePanel.getGraphics(),Color.red,query);
            }
            if(cbB.isSelected()){
            String query="select shape from building s where mdsys.sdo_relate(s.shape, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=ANYINTERACT') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentBuilding(imagePanel.getGraphics(),Color.yellow,query);
            }
            if(cbS.isSelected()){
            String query="select location from student s where mdsys.sdo_relate(s.location, MDSYS.SDO_GEOMETRY(2003,null,null,MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,1),MDSYS.SDO_ORDINATE_ARRAY("+arg+")),'mask=INSIDE+COVEREDBY+INTERSECT') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentStudent(imagePanel.getGraphics(),Color.green,query);
            }
            }
            }
        }
        if(rbSS.isSelected())
        {
            if(pointQuery==0)
            { 
                JOptionPane.showMessageDialog(null,"Please select a point on Map");
            }
            else{
            String arg=mouseX+","+mouseY;
            String query="select shape from announcement_system s where mdsys.sdo_nn(s.center, MDSYS.SDO_GEOMETRY(2001,null,MDSYS.SDO_POINT_TYPE("+arg+",null),null,null),'sdo_num_res=1') = 'TRUE'";
            queryTextList.append("Query "+queryCount+". "+query+"\n");
            paintComponentStudent_SS(imagePanel.getGraphics(),Color.red,query,queryCount);}
        }
        if(rbEQ.isSelected())
        {
            if(pointQuery==0)
            { 
                JOptionPane.showMessageDialog(null,"Please select a point on Map");
            }
            else{
            paintComponentStudent_AS_EQ(imagePanel.getGraphics(),EQquery,IDEQquery,queryCount);
            }
        }
        }
    }//GEN-LAST:event_submitQuery

    private void rbWRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbWRActionPerformed
        clear();
        imagePanel.repaint();
    }//GEN-LAST:event_rbWRActionPerformed

    private void rbPQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPQActionPerformed
        clear();
        imagePanel.repaint();
    }//GEN-LAST:event_rbPQActionPerformed

    private void rbRQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRQActionPerformed
        clear();
        imagePanel.repaint();
    }//GEN-LAST:event_rbRQActionPerformed

    private void rbSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSSActionPerformed
        clear();
        imagePanel.repaint();
    }//GEN-LAST:event_rbSSActionPerformed

    private void rbEQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbEQActionPerformed
        clear();
        imagePanel.repaint();
    }//GEN-LAST:event_rbEQActionPerformed

    private void cbASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbASActionPerformed
        imagePanel.repaint();
        clear();
//        if(rbPQ.isSelected() || rbRQ.isSelected())
//        {
//            clear();
//        }
//        
    }//GEN-LAST:event_cbASActionPerformed

    private void cbBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBActionPerformed
        imagePanel.repaint();
        clear();
//        if(rbPQ.isSelected() || rbRQ.isSelected())
//        {
//            clear();
//        }
    }//GEN-LAST:event_cbBActionPerformed

    private void cbSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSActionPerformed
        imagePanel.repaint();
        clear();
//        if(rbPQ.isSelected() || rbRQ.isSelected())
//        {
//            clear();
//        }
        
    }//GEN-LAST:event_cbSActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbAS;
    private javax.swing.JCheckBox cbB;
    private javax.swing.JCheckBox cbS;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea queryTextList;
    private javax.swing.JRadioButton rbEQ;
    private javax.swing.JRadioButton rbPQ;
    private javax.swing.JRadioButton rbRQ;
    private javax.swing.JRadioButton rbSS;
    private javax.swing.JRadioButton rbWR;
    private javax.swing.JTextField xTxt;
    private javax.swing.JTextField yTxt;
    // End of variables declaration//GEN-END:variables
}
