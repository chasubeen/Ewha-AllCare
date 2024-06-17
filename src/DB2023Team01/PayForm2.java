//필요한 패키지랑 클래스 import
package DB2023Team01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

//PAYMENT inert창 만들기
public class PayForm2 extends JFrame implements ActionListener {

	// 입력받을 요소의 이름을 라벨로 만들어준다
	private JLabel Setlabel1 = new JLabel("학생 이름: ");
	private JLabel Setlabel2 = new JLabel("강의 이름: ");
	private JLabel Setlabel3 = new JLabel("월: ");
	private JLabel Setlabel4 = new JLabel("금액: ");
	private JLabel Setlabel5 = new JLabel("납부일자: ");

	// 입력받을 요소들을 적을 공간을 만들어준다
	private JTextField fsetST = new JTextField(10);
	private JTextField fsetC = new JTextField(10);
	private JTextField fsetMon = new JTextField(5);
	private JTextField fsetFee = new JTextField(10);
	private JTextField fsetPay_ymd = new JTextField(10);

	// 입력이 완료되면 누를 버튼을 만들어준다
	private JButton ADD_Button = new JButton("수강료납부등록");

	public PayForm2() {
		// 화면의 구성을 위해 각 panel을 만들어 배치한다.
		JPanel TotalPanel = new JPanel();
		JPanel Panel1 = new JPanel();
		JPanel Panel2 = new JPanel();
		JPanel Panel3 = new JPanel();
		JPanel Panel4 = new JPanel();
		JPanel Panel5 = new JPanel();

		// 위에서 만든 라벨과 그에 맞는 textbox를 각각의 panel에 배치한다.
		Panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel1.add(Setlabel1);
		Panel1.add(fsetST);

		Panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel2.add(Setlabel2);
		Panel2.add(fsetC);

		Panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel3.add(Setlabel3);
		Panel3.add(fsetMon);

		Panel4.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel4.add(Setlabel4);
		Panel4.add(fsetFee);

		Panel5.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel5.add(Setlabel5);
		Panel5.add(fsetPay_ymd);

		// 여러 panel을 totalpanel에 배치한다.
		TotalPanel.setLayout(new BoxLayout(TotalPanel, BoxLayout.Y_AXIS));
		TotalPanel.add(Panel1);
		TotalPanel.add(Panel2);
		TotalPanel.add(Panel3);
		TotalPanel.add(Panel4);
		TotalPanel.add(Panel5);

		TotalPanel.add(ADD_Button);

		ADD_Button.addActionListener(this);

		setTitle("EWHA All Care-Payment");
		setSize(350, 500);
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

		// INSERT 쿼리
		String query = "INSERT INTO DB2023_PAYMENT (p_id,e_id,a_id,mon,fee,pay_ymd) " + "VALUES (?, ?, ?, ?, ?, ?)";
		// SELECT 쿼리
		String query1 = "SELECT st_id from DB2023_STUDENT where st_nm=?";
		String query2 = "SELECT c_id from DB2023_COURSE where c_nm=?";
		String query3 = "SELECT e_id from DB2023_ST_CLASS_LOG where st_id=? and c_id=?";
		String query4 = "SELECT COUNT(p_id) FROM DB2023_PAYMENT";

		// 각 빈칸의 입력값을 받아드린다. 이 때,String이 아닌 int값은 변경을해준다.

		int setMon = Integer.parseInt(fsetMon.getText());
		int setFee = Integer.parseInt(fsetFee.getText());
		String setPay_ymd = fsetPay_ymd.getText();
		String setST = fsetST.getText();
		String setC = fsetC.getText();
		int setAttendance = 8;

		Connection conn = null;
		PreparedStatement p1 = null;
		PreparedStatement p2 = null;
		PreparedStatement p3 = null;
		PreparedStatement p4 = null;
		PreparedStatement p = null;

		try {
			conn = DriverManager.getConnection(url, user, pwd);
			conn.setAutoCommit(false); // 트랜잭션 시작

			// p_id 개수 가져오기=> auto increasing이므로
			p4 = conn.prepareStatement(query4);
			ResultSet rs = p4.executeQuery();
			int pIdCount = 0;
			if (rs.next()) {
				pIdCount = rs.getInt(1);
			}
			rs.close();
			p4.close();

			// 학생이름으로 학생아이디를 가지고 온다
			p1 = conn.prepareStatement(query1);
			p1.setString(1, setST);
			ResultSet rs1 = p1.executeQuery();

			if (rs1.next()) {
				String st_id = rs1.getString("st_id");

				// 강의이름으로 강의아이디를 가지고 온다
				p2 = conn.prepareStatement(query2);
				p2.setString(1, setC);
				ResultSet rs2 = p2.executeQuery();

				if (rs2.next()) {
					String c_id = rs2.getString("c_id");

					// 학생아이디와 강의아이디로 등록아이디를 가지고 온다
					p3 = conn.prepareStatement(query3);
					p3.setString(1, st_id);
					p3.setString(2, c_id);
					ResultSet rs3 = p3.executeQuery();

					if (rs3.next()) {
						String seteid = rs3.getString("e_id");

						// 입력받은값과 그값으로 얻은 값을 DB2023_PAYMENT에 반영한다.
						p = conn.prepareStatement(query);
						p.setString(1, Integer.toString(pIdCount + 1)); // 새로운 p_id 설정
						p.setString(2, seteid);
						p.setInt(3, setAttendance);
						p.setInt(4, setMon);
						p.setInt(5, setFee);
						p.setString(6, setPay_ymd);

						p.executeUpdate();
						conn.commit(); // 트랜잭션 커밋

						// 업데이트 완료 후 PayForm2 창 닫기
						JOptionPane.showMessageDialog(null, "조회를 누르면 반영된 수강료납부기록을 볼 수 있습니다.");
						setVisible(false);
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "해당하는 수강료납부기록을 찾을 수 없습니다!");
						conn.rollback(); // 트랜잭션 롤백
					}
					rs3.close();
					p3.close();
				} else {
					JOptionPane.showMessageDialog(this, "해당하는 수강료납부기록을 찾을 수 없습니다!");
					conn.rollback(); // 트랜잭션 롤백
				}
				rs2.close();
				p2.close();
			} else {
				JOptionPane.showMessageDialog(this, "해당하는 수강료납부기록을 찾을 수 없습니다!");
				conn.rollback(); // 트랜잭션 롤백
			}
			rs1.close();
			p1.close();
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
				if (p != null) {
					p.close();
				}
				if (p3 != null) {
					p3.close();
				}
				if (p2 != null) {
					p2.close();
				}
				if (p1 != null) {
					p1.close();
				}
				if (conn != null) {
					conn.setAutoCommit(true); // 트랜잭션 종료 후 자동 커밋으로 설정
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
				new PayForm2();

			}

		});
	}
}