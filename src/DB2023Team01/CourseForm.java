package DB2023Team01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CourseForm extends JFrame implements ActionListener {
	private JLabel Setlabel1 = new JLabel("강좌ID: ");
	private JLabel Setlabel2 = new JLabel("권장학년: ");
	private JLabel Setlabel3 = new JLabel("강사이름: ");
	private JLabel Setlabel4 = new JLabel("강의명: ");
	private JLabel Setlabel5 = new JLabel("수업요일: ");
	private JLabel Setlabel6 = new JLabel("강의실: ");
	private JLabel Setlabel7 = new JLabel("시작시간: ");
	private JLabel Setlabel8 = new JLabel("종료시간: ");

	private JTextField setCID = new JTextField(10);
	private JTextField setRED_GRADE = new JTextField(10);
	private JTextField setM_NM = new JTextField(10);
	private JTextField setC_NM = new JTextField(10);
	private JTextField setDAY = new JTextField(10);
	private JTextField setROOM = new JTextField(10);
	private JTextField setS_TIME = new JTextField(10);
	private JTextField setE_TIME = new JTextField(10);

	private JButton ADD_Button = new JButton("강좌등록");

	public CourseForm() {
		JPanel TotalPanel = new JPanel();
		JPanel Panel1 = new JPanel();
		JPanel Panel2 = new JPanel();
		JPanel Panel3 = new JPanel();
		JPanel Panel4 = new JPanel();
		JPanel Panel5 = new JPanel();
		JPanel Panel6 = new JPanel();
		JPanel Panel7 = new JPanel();
		JPanel Panel8 = new JPanel();

		Panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel1.add(Setlabel1);
		Panel1.add(setCID);

		Panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel2.add(Setlabel2);
		Panel2.add(setRED_GRADE);

		Panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel3.add(Setlabel3);
		Panel3.add(setM_NM);

		Panel4.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel4.add(Setlabel4);
		Panel4.add(setC_NM);

		Panel5.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel5.add(Setlabel5);
		Panel5.add(setDAY);

		Panel6.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel6.add(Setlabel6);
		Panel6.add(setROOM);

		Panel7.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel7.add(Setlabel7);
		Panel7.add(setS_TIME);

		Panel8.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel8.add(Setlabel8);
		Panel8.add(setE_TIME);

		TotalPanel.setLayout(new BoxLayout(TotalPanel, BoxLayout.Y_AXIS));
		TotalPanel.add(Panel1);
		TotalPanel.add(Panel2);
		TotalPanel.add(Panel3);
		TotalPanel.add(Panel4);
		TotalPanel.add(Panel5);
		TotalPanel.add(Panel6);
		TotalPanel.add(Panel7);
		TotalPanel.add(Panel8);
		TotalPanel.add(ADD_Button);

		ADD_Button.addActionListener(this);

		setTitle("EWHA All Care-Enrollment");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		String query = "INSERT INTO DB2023_COURSE (c_id, rec_grade, m_id, c_nm, week_day, room, start_time, end_time) "
				+ "VALUES (?, ?, (SELECT m_id FROM DB2023_MEMBER WHERE m_nm = ?), ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, user, pwd);
				PreparedStatement statement = conn.prepareStatement(query)) {

			// 쿼리에 필요한 매개변수 설정
			statement.setString(1, setCID.getText());
			statement.setString(2, setRED_GRADE.getText());
			statement.setString(3, setM_NM.getText());
			statement.setString(4, setC_NM.getText());
			statement.setString(5, setDAY.getText());
			statement.setString(6, setROOM.getText());
			statement.setString(7, setS_TIME.getText());
			statement.setString(8, setE_TIME.getText());

			// 쿼리 실행
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				JOptionPane.showMessageDialog(this, "강좌가 등록되었습니다. 등록된 강좌를 확인하기 위헤 조회 버튼을 다시 눌러주세요.");
			} else {
				JOptionPane.showMessageDialog(this, "강좌 등록에 실패했습니다.");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CourseForm();
			}

		});
	}
}
