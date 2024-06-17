package DB2023Team01;

//필요한 라이브러리 import
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

//PAYMENT update창 만들기
public class PayForm extends JFrame implements ActionListener {

	//입력받을 요소의 이름을 라벨로 만들어준다
    private JLabel Setlabel1 = new JLabel("출석한 수: ");
    private JLabel Setlabel2 = new JLabel("환불일자: ");

  //입력받을 요소들을 적을 공간을 만들어준다
    private JTextField fsetAttendance = new JTextField(5);
    private JTextField fsetPayback_ymd = new JTextField(10);

  //입력이 완료되면 누를 버튼을 만들어준다
    private JButton UPDATE_Button = new JButton("업데이트");
   
    //수정할 수납기록을 지정한다
    public int setPid;

    public PayForm(int setPid) {
    	//지정된 수납기록을 사용할 수 있도록한다.
    	this.setPid = setPid;
    	
    	//화면의 구성을 위해 각 panel을 만들어 배치한다.
        JPanel TotalPanel = new JPanel();
        JPanel Panel1 = new JPanel();
        JPanel Panel2 = new JPanel();

        //위에서 만든 라벨과 그에 맞는 textbox를 각각의 panel에 배치한다.
        Panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        Panel1.add(Setlabel1);
        Panel1.add(fsetAttendance);

        Panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        Panel2.add(Setlabel2);
        Panel2.add(fsetPayback_ymd);

      //여러 panel을 totalpanel에 배치한다.
        TotalPanel.setLayout(new BoxLayout(TotalPanel, BoxLayout.Y_AXIS));
        TotalPanel.add(Panel1);
        TotalPanel.add(Panel2);

        TotalPanel.add(UPDATE_Button);

        UPDATE_Button.addActionListener(this);

        setTitle("EWHA All Care-Payment");
        setSize(350,300);
        setLocationRelativeTo(null);
        setVisible(true);

        add(TotalPanel);
    }

    public void actionPerformed(ActionEvent e) {
        // DB 연결 정보
        String user = "DB2023Team01";
        String pwd = "DB2023Team01";
        String dbname = "DB2023Team01";
        String url = "jdbc:mysql://localhost:3306/" + dbname
                + "?useOldAliasMetadataBehavior=true&characterEncoding=utf8&serverTimezone=UTC";

        // UPDATE 쿼리
        String query1 = "UPDATE DB2023_PAYMENT SET a_id=? WHERE p_id=?";
        String query2 = "UPDATE DB2023_PAYMENT SET pay_back=? WHERE p_id=?";
        String query3 = "UPDATE DB2023_PAYMENT SET payback_ymd=? WHERE p_id=?";
        // SELECT 쿼리
        String query4="SELECT fee FROM DB2023_PAYMENT WHERE p_id=? ";

     // 각 빈칸의 입력값을 받아드린다. 이 때,String이 아닌 int값은 변경을해준다.
        int setAttendance = Integer.parseInt(fsetAttendance.getText());
        String setPayback_ymd = fsetPayback_ymd.getText();
        int setPayback=0;

        Connection conn = null;
        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        PreparedStatement p3 = null;
        PreparedStatement p4 = null;

        try {
            conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            //지정된 p_id로 해당 달에 낸 수강료를 가지고 온다.
            p4=conn.prepareStatement(query4);
            p4.setInt(1, setPid); 
            
            ResultSet r=p4.executeQuery();
            if (r.next()) {
            	setPayback= r.getInt(1);
            }
            //환불할 금액을 계산한다
            setPayback=setPayback-setPayback*setAttendance/8;

            //입력받은 값으로 각각의 속성을 업데이트해준다.
            p1 = conn.prepareStatement(query1);
            p1.setInt(1, setAttendance);
            p1.setInt(2, setPid); // 수정: setPid를 int로 설정
            p1.executeUpdate();

            p2 = conn.prepareStatement(query2);
            p2.setInt(1, setPayback);
            p2.setInt(2, setPid); // 수정: setPid를 int로 설정
            p2.executeUpdate();

            p3 = conn.prepareStatement(query3);
            p3.setString(1, setPayback_ymd);
            p3.setInt(2, setPid); // 수정: setPid를 int로 설정
            p3.executeUpdate();

            conn.commit(); // 트랜잭션 커밋
            JOptionPane.showMessageDialog(this, "업데이트된 수강료정보는 조회버튼을 누르면 반영되어 볼 수 있습니다.");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback(); // 트랜잭션 롤백
                }
            } catch (SQLException ex2) {
                ex2.printStackTrace();
            }
        } finally {
            try {
                if (p1 != null) {
                    p1.close();
                }
                if (p2 != null) {
                    p2.close();
                }
                if (p3 != null) {
                    p3.close();
                }
                if (p4 != null) {
                    p4.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	int setPid = Integer.parseInt(JOptionPane.showInputDialog("수정할 수강료납부의 ID를 입력하세요."));
            	String p=Integer.toString(setPid);
        		if (p == null) {
        			return; // 취소 버튼이 클릭되었을 경우 메서드 종료
        		}
                new PayForm(setPid);
            }
        });
    }
}
