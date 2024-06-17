package DB2023Team01;

import javax.swing.*;

import DB2023Team01.CourseForm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MemberForm extends JFrame implements ActionListener {
	private JLabel Setlabel1 = new JLabel("직급: ");
	private JLabel Setlabel2 = new JLabel("직원 이름: ");
	private JLabel Setlabel3 = new JLabel("전화번호: ");
	private JLabel Setlabel4 = new JLabel("급여상세: ");
	private JLabel Setlabel5 = new JLabel("입사날짜: ");
	private JLabel Setlabel6 = new JLabel("근무상태: ");

	private JTextField setDP = new JTextField(10);
	private JTextField setM_NM = new JTextField(10);
	private JTextField setM_PH = new JTextField(10);
	private JTextField setSalary = new JTextField(10);
	private JTextField setWork_in = new JTextField(10);
	private JTextField setStatus = new JTextField(10);

	private JButton ADD_Button = new JButton("직원 등록");

	public MemberForm() {
		JPanel TotalPanel = new JPanel();
		JPanel Panel1 = new JPanel();
		JPanel Panel2 = new JPanel();
		JPanel Panel3 = new JPanel();
		JPanel Panel4 = new JPanel();
		JPanel Panel5 = new JPanel();
		JPanel Panel6 = new JPanel();

		Panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel1.add(Setlabel1);
		Panel1.add(setDP);

		Panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel2.add(Setlabel2);
		Panel2.add(setM_NM);

		Panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel3.add(Setlabel3);
		Panel3.add(setM_PH);

		Panel4.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel4.add(Setlabel4);
		Panel4.add(setSalary);

		Panel5.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel5.add(Setlabel5);
		Panel5.add(setWork_in);

		Panel6.setLayout(new FlowLayout(FlowLayout.LEFT));
		Panel6.add(Setlabel6);
		Panel6.add(setStatus);

		TotalPanel.setLayout(new BoxLayout(TotalPanel, BoxLayout.Y_AXIS));
		TotalPanel.add(Panel1);
		TotalPanel.add(Panel2);
		TotalPanel.add(Panel3);
		TotalPanel.add(Panel4);
		TotalPanel.add(Panel5);
		TotalPanel.add(Panel6);
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
		String query = "INSERT INTO DB2023_MEMBER (dp_type, m_nm, m_ph, salary_detail, work_in_ymd, work_status) "
				+ "VALUES ((SELECT dp_type FROM DB2023_DEPARTMENT WHERE dp_nm = ?), ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, user, pwd);
				PreparedStatement statement = conn.prepareStatement(query)) {

			// 쿼리에 필요한 매개변수 설정
			statement.setString(1, setDP.getText());
			statement.setString(2, setM_NM.getText());
			statement.setString(3, setM_PH.getText());
			statement.setString(4, setSalary.getText());
			statement.setString(5, setWork_in.getText());
			statement.setString(6, setStatus.getText());

			// 쿼리 실행
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				JOptionPane.showMessageDialog(this, "직원이 등록되었습니다. 등록된 직원 확인하기 위헤 조회 버튼을 다시 눌러주세요.");
			} else {
				JOptionPane.showMessageDialog(this, "직원 등록에 실패했습니다.");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MemberForm();
			}

		});
	}
}
