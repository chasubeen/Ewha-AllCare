/*필요한 패키지랑 클래스 import*/
package DB2023Team01;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Pay extends JFrame implements ActionListener {

	private JComboBox<String> Category;
	private JComboBox<String> Y;
	private JComboBox<String> mon;

	//조회시 보여줄 attribute를 적어준다
	private String[] columnNames = { "p_id", "s_nm", "c_nm", "a_id","mon", "fee", "pay_ymd", "pay_back", "payback_ymd" };

	private DefaultTableModel tableModel;
	private JTable table;

	//DB2023_PAYMENT의 각각의 기능을 나타내는 버튼을 만든다
	private JButton searchButton;
	private JButton updateButton;
	private JButton insertButton;
	private JButton deleteButton;
	
	private Connection conn;

	public Pay() {
		//필터를 걸어줄 요소를 적어준다
		Category = new JComboBox<>(new String[] { "전체", "년도별", "월별" });
		//필터=>년도
		Y = new JComboBox<>(new String[] { "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032" });
		//필터=>월별
		mon = new JComboBox<>(new String[]  {"1","2","3","4","5","6","7","8","9","10","11","12"} );

		Y.setVisible(false); // 처음에는 보이지 않도록 설정
		mon.setVisible(false); // 처음에는 보이지 않도록 설정

		// Category 선택에 따라 YEAR과 MONTH가 활성화됨
		Category.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCategory = Category.getSelectedItem().toString();
				Y.setVisible(selectedCategory.equals("년도별"));
				mon.setVisible(selectedCategory.equals("월별"));
			}
		});
		
		//필터들을 넣어두는 panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("검색 범위 "));
		topPanel.add(Category);
		topPanel.add(Y);
		topPanel.add(mon);

		//버튼들을 넣어두는 panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchButton = new JButton("조회");
		updateButton = new JButton("수정");
		insertButton=new JButton("등록");
		deleteButton = new JButton("삭제");
		buttonPanel.add(searchButton);
		buttonPanel.add(updateButton);
		buttonPanel.add(insertButton);
		buttonPanel.add(deleteButton);
		
		//조회된 결과를 보여주기 위해
		tableModel = new DefaultTableModel(columnNames, 0);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		//모든 panel을 메인 panel에 배치한다
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		searchButton.addActionListener(this);
		updateButton.addActionListener(this);
		insertButton.addActionListener(this);
		deleteButton.addActionListener(this);

		setTitle("EWHA All Care_Payment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 450);
		setLocationRelativeTo(null);
		setVisible(true);

		/* DB 연결 */
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC 드라이버 연결

			// 사용자 인증
			String user = "DB2023Team01";
			String pwd = "DB2023Team01";
			String dbname = "DB2023Team01";
			String url = "jdbc:mysql://localhost:3306/" + dbname
					+ "?useOldAliasMetadataBehavior=true&characterEncoding=utf8&serverTimezone=UTC";

			conn = DriverManager.getConnection(url, user, pwd);

			System.out.println("정상적으로 연결되었습니다.");
			
			// 모든 수강료납부 조회
	        searchPay();

		} catch (SQLException e1) {
			System.err.println("연결할 수 없습니다.");
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.err.println("드라이버를 로드할 수 없습니다.");
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchButton) {
			searchPay(); // 수강료납부 조회
		} else if (e.getSource() == updateButton) {
			updatePay(); // 수강료납부 수정
		}  else if (e.getSource() == insertButton) {
			insertPay(); // 수강료납부 등록
		}else if (e.getSource() == deleteButton) {
			deletePay(); // 수강료납부 삭제
		}
	}
	
	//수강료납부조회함수
	private void searchPay() {
		String category = Category.getSelectedItem().toString();

		String query = "SELECT * FROM DB2023_PAYMENT_VIEW";// 새로운 뷰만들기

		if (category.equals("월별")) {
			// 월별 조회
			String selectedmonString = mon.getSelectedItem().toString();
			try {
				int selectedmon = Integer.parseInt(selectedmonString);
				if (selectedmon >= 1 && selectedmon <= 12) {
					query += " WHERE mon = " + selectedmon + ";";
				} else {
					System.out.println("선택된 월이 유효하지 않습니다.1~12 사이의 값을 선택해주세요.");
				}
			} catch (NumberFormatException e2) {
				System.out.println("선택된 월을 정수로 변환할 수 없습니다.");
			}
		} else if (category.equals("년도별")) {//년도별조회
			String selectedYearString = Y.getSelectedItem().toString();
			try {
				int selectedYear = Integer.parseInt(selectedYearString);
				if (selectedYear >= 2023 && selectedYear <= 2032) {
					query += " WHERE YEAR(pay_ymd) = " + selectedYear + ";";
				} else {
					System.out.println("선택된 년도이 유효하지 않습니다.2023~2032 사이의 값을 선택해주세요.");
				}
			} catch (NumberFormatException e2) {
				System.out.println("선택된 년도를 정수로 변환할 수 없습니다.");
			}
		}

		try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			tableModel.setRowCount(0);
			while (resultSet.next()) {
				String[] rowData = new String[tableModel.getColumnCount()];
				for (int i = 0; i < tableModel.getColumnCount(); i++) {
					rowData[i] = resultSet.getString(tableModel.getColumnName(i));
				}
				tableModel.addRow(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// 수강료납부 조회 완료

	//수강료납부 수정 
	private void updatePay() {
		//수정할 수강료납부ID를 지정한다
    	int setPid = Integer.parseInt(JOptionPane.showInputDialog("수정할 수강료납부의 ID를 입력하세요."));
    	String p=Integer.toString(setPid);
		if (p == null) {
			return; // 취소 버튼이 클릭되었을 경우 메서드 종료
		}
		//수정창을 생성하는 PayForm실행
		PayForm ppp=new PayForm(setPid);
		
	}
	//수강료납부 수정 완료 

	// 수강료납부 등록 
	private void insertPay() {
		// 입력창을 생성하는 PayForm2 실행
        PayForm2 pp = new PayForm2();
        
	}
	//수강료납부등록 완료
	
	//수강료납부 삭제 
	private void deletePay() {
		// 삭제할 수강료납부의 ID를 지정한다
		String pId = JOptionPane.showInputDialog("삭제할 수강료납부의 ID를 입력하세요.");
		if (pId == null) {
			return; // 취소 버튼이 클릭되었을 경우 메서드 종료
		}

		try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM DB2023_PAYMENT WHERE p_id = ?")) {
			deleteStmt.setString(1, pId);
			int deletedRows = deleteStmt.executeUpdate();
			if (deletedRows > 0) {
				JOptionPane.showMessageDialog(null, "수강료납부가 삭제되었습니다.");
				searchPay(); // 변경 사항을 반영하여 테이블을 업데이트한다.
			} else {
				JOptionPane.showMessageDialog(null, "삭제할 수강료납부를 찾을 수 없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//수강료납부 삭제 완료

	public static void main(String[] args) {
		new Pay();
	}

}