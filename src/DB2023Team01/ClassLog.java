package DB2023Team01;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;

public class ClassLog extends JFrame {

	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/";
	private static final String USER = "DB2023Team01"; // MySQL 사용자 이름
	private static final String PASS = "DB2023Team01"; // MySQL 비밀번호

	private Statement statement;
	PreparedStatement pstmt = null;
	Connection conn = null;

	private JTable logTable;
	private JTextField e_idField;
	private JTextField st_idField;
	private JTextField st_nmField;
	private JTextField c_idField;
	private JTextField c_nmField;
	private JTextField m_idField;
	private JTextField m_nmField;
	private JTextField start_ymdField;
	private JTextField end_ymdField;

	public ClassLog() {
		System.out.println("DB에 연결중입니다 . . .");
		try {
			// MySQL 드라이버 로드
			Class.forName(DRIVER);

			// 데이터베이스 연결
			conn = DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e1) {
			System.err.println("DB에 연결할 수 없습니다.");
			e1.printStackTrace();
		} catch (ClassNotFoundException e2) {
			System.err.println("MySQL Driver를 로드할 수 없습니다.");
			e2.printStackTrace();
		}
		System.out.println("정상적으로 DB에 연결되었습니다.");

		try {
			// SQL 문 실행을 위한 Statement 객체 생성
			statement = conn.createStatement();

			// GUI 구성 요소 초기화
			e_idField = new JTextField(10);
			st_idField = new JTextField(20);
			st_nmField = new JTextField(20);
			c_idField = new JTextField(15);
			c_nmField = new JTextField(20);
			m_idField = new JTextField(10);
			m_nmField = new JTextField(20);

			/* start_ymd 예시 형식 보여주기 "0000-00-00" */
			start_ymdField = new JTextField(10);
			start_ymdField.setText(""); // 기본값을 비워둡니다.
			start_ymdField.setForeground(Color.GRAY); // 연한 회색으로 텍스트 색상 설정
			start_ymdField.setText("0000-00-00"); // 힌트로 보여줄 예시 텍스트 설정

			// 포커스 이벤트 리스너 추가
			start_ymdField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					// 텍스트 필드에 포커스를 얻을 때
					if (start_ymdField.getText().equals("0000-00-00")) {
						start_ymdField.setText(""); // 기존의 예시 텍스트를 지웁니다.
						start_ymdField.setForeground(Color.BLACK); // 텍스트 색상을 검은색으로 변경합니다.
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					// 텍스트 필드에서 포커스를 잃을 때
					if (start_ymdField.getText().isEmpty()) {
						start_ymdField.setText("0000-00-00"); // 텍스트 필드가 비어있으면 다시 예시 텍스트를 보여줍니다.
						start_ymdField.setForeground(Color.GRAY); // 텍스트 색상을 연한 회색으로 변경합니다.
					}
				}
			});

			/* end_ymd 예시 형식 보여주기 "0000-00-00" */
			end_ymdField = new JTextField(10);
			end_ymdField.setText(""); // 기본값을 비워둡니다.
			end_ymdField.setForeground(Color.GRAY); // 연한 회색으로 텍스트 색상 설정
			end_ymdField.setText("0000-00-00"); // 힌트로 보여줄 예시 텍스트 설정

			// 포커스 이벤트 리스너 추가
			end_ymdField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					// 텍스트 필드에 포커스를 얻을 때
					if (end_ymdField.getText().equals("0000-00-00")) {
						end_ymdField.setText(""); // 기존의 예시 텍스트를 지웁니다.
						end_ymdField.setForeground(Color.BLACK); // 텍스트 색상을 검은색으로 변경합니다.
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					// 텍스트 필드에서 포커스를 잃을 때
					if (end_ymdField.getText().isEmpty()) {
						end_ymdField.setText("0000-00-00"); // 텍스트 필드가 비어있으면 다시 예시 텍스트를 보여줍니다.
						end_ymdField.setForeground(Color.GRAY); // 텍스트 색상을 연한 회색으로 변경합니다.
					}
				}
			});

			JButton addButton = new JButton("추가");
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addMember();
				}
			});

			JButton updateButton = new JButton("수정");
			updateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateMember();
				}
			});

			JButton deleteButton = new JButton("삭제");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteMember();
				}
			});

			JButton viewButton = new JButton("조회");
			viewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewAllMembers();
				}
			});

			// 입력 패널 생성 및 구성 요소 추가
			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			inputPanel.add(new JLabel("학생 이름 : "));
			inputPanel.add(st_nmField);
			inputPanel.add(new JLabel("수업 이름 : "));
			inputPanel.add(c_nmField);
			inputPanel.add(new JLabel("강사 이름 : "));
			inputPanel.add(m_nmField);
			inputPanel.add(new JLabel("수강 시작 날짜 : "));
			inputPanel.add(start_ymdField);
			inputPanel.add(new JLabel("수강 종료 날짜 : "));
			inputPanel.add(end_ymdField);

			// 버튼 패널 생성 및 버튼 추가
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(viewButton);
			buttonPanel.add(addButton);
			buttonPanel.add(updateButton);
			buttonPanel.add(deleteButton);

			// 수강내역 목록 테이블 모델 생성
			DefaultTableModel tableModel = new DefaultTableModel();
			tableModel.addColumn("수강 내역 id");
			tableModel.addColumn("학생 이름");
			tableModel.addColumn("수업 이름");
			tableModel.addColumn("강사 이름");
			tableModel.addColumn("수강 시작 날짜");
			tableModel.addColumn("수강 종료 날짜");

			logTable = new JTable(tableModel);

			JScrollPane scrollPane = new JScrollPane(logTable);

			// 프레임에 패널 및 테이블 추가
			add(inputPanel, BorderLayout.NORTH);
			add(buttonPanel, BorderLayout.SOUTH);
			add(scrollPane, BorderLayout.CENTER);

			// 전체 강좌 목록 조회
			viewAllMembers();

			// JFrame 설정
			setTitle("EWHA ALL CARE_수강 내역");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			pack();
			setLocationRelativeTo(null); // 화면 중앙에 표시
			setVisible(true);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 수강 내역 추가 메서드
	private void addMember() {
		try {
			conn.setAutoCommit(false);

			// 입력된 수강 내역 관련 정보 가져오기
			String st_nm = st_nmField.getText();
			String c_nm = c_nmField.getText();
			String m_nm = m_nmField.getText();
			String start_ymd = start_ymdField.getText();
			String end_ymd = end_ymdField.getText();

			// 학생 ID 조회
			String stIdQuery = "SELECT st_id FROM DB2023_STUDENT WHERE st_nm = ?";
			PreparedStatement stIdStmt = conn.prepareStatement(stIdQuery);
			stIdStmt.setString(1, st_nm);
			ResultSet stIdResult = stIdStmt.executeQuery();
			if (stIdResult.next()) {
				String st_id = stIdResult.getString("st_id");

				// 강좌 ID 조회
				String cIdQuery = "SELECT c_id FROM DB2023_COURSE WHERE c_nm = ?";
				PreparedStatement cIdStmt = conn.prepareStatement(cIdQuery);
				cIdStmt.setString(1, c_nm);
				ResultSet cIdResult = cIdStmt.executeQuery();
				if (cIdResult.next()) {
					String c_id = cIdResult.getString("c_id");

					// 강사 ID 조회
					String mIdQuery = "SELECT m_id FROM DB2023_MEMBER WHERE m_nm = ?";
					PreparedStatement mIdStmt = conn.prepareStatement(mIdQuery);
					mIdStmt.setString(1, m_nm);
					ResultSet mIdResult = mIdStmt.executeQuery();
					if (mIdResult.next()) {
						String m_id = mIdResult.getString("m_id");

						// INSERT 쿼리 실행
						String insertQuery = "INSERT INTO DB2023_ST_CLASS_LOG (st_id, c_id, m_id, start_ymd, end_ymd) VALUES (?, ?, ?, ?, ?)";
						pstmt = conn.prepareStatement(insertQuery);
						pstmt.setString(1, st_id);
						pstmt.setString(2, c_id);
						pstmt.setString(3, m_id);
						pstmt.setString(4, start_ymd);
						if (end_ymd.equals("null") || end_ymd.isEmpty() || end_ymd == null) {
							pstmt.setNull(5, Types.DATE);
						} else {
							pstmt.setString(5, end_ymd);
						}
						pstmt.executeUpdate();
						conn.commit();

						viewAllMembers();

						clearFields();
					} else {
						JOptionPane.showMessageDialog(this, "해당하는 강사의 이름을 찾을 수 없습니다!");
					}
					mIdResult.close();
					mIdStmt.close();
				} else {
					JOptionPane.showMessageDialog(this, "해당하는 강좌의 이름을 찾을 수 없습니다!");
				}
				cIdResult.close();
				cIdStmt.close();
			} else {
				JOptionPane.showMessageDialog(this, "해당하는 학생의 이름을 찾을 수 없습니다!");
			}
			stIdResult.close();
			stIdStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 수강 내역 수정 메서드
	private void updateMember() {
	    logTable.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent e) {
	            if (e.getClickCount() == 2) {
	                int selectedRow = logTable.getSelectedRow();
	                if (selectedRow != -1) {
	                    String st_nm = (String) logTable.getValueAt(selectedRow, 1);
	                    String c_nm = (String) logTable.getValueAt(selectedRow, 2);
	                    String m_nm = (String) logTable.getValueAt(selectedRow, 3);
	                    String start_ymd = (String) logTable.getValueAt(selectedRow, 4);
	                    String end_ymd = (String) logTable.getValueAt(selectedRow, 5);

	                    st_nmField.setText(st_nm);
	                    c_nmField.setText(c_nm);
	                    m_nmField.setText(m_nm);
	                    start_ymdField.setText(start_ymd);
	                    end_ymdField.setText(end_ymd);
	                }
	            }
	        }
	    });

	    try {
	        conn.setAutoCommit(false);

	        int selectedRow = logTable.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(this, "수정할 튜플을 선택해주세요.");
	            return;
	        }

	        String e_id = (String) logTable.getValueAt(selectedRow, 0);
	        String start_ymd = start_ymdField.getText();
	        String end_ymd = end_ymdField.getText();

	        // 값이 입력되지 않은 경우 안내 메시지 표시
	        if (st_nmField.getText().isEmpty() || c_nmField.getText().isEmpty() || m_nmField.getText().isEmpty() || start_ymd.isEmpty() || end_ymd.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "모든 필드에 값을 입력해주세요.");
	            return;
	        }

	        String updateQuery = "UPDATE DB2023_ST_CLASS_LOG " + "SET start_ymd = ?, end_ymd = ? " + "WHERE e_id = ?";
	        pstmt = conn.prepareStatement(updateQuery);
	        pstmt.setString(1, start_ymd);
	        if (end_ymd.equals("null") || end_ymd.isEmpty() || end_ymd == null) {
	            pstmt.setNull(2, Types.DATE);
	        } else {
	            pstmt.setString(2, end_ymd);
	        }
	        pstmt.setString(3, e_id);
	        int rowsAffected = pstmt.executeUpdate();

	        if (rowsAffected > 0) {
	            conn.commit();
	            JOptionPane.showMessageDialog(this, "수강 내역이 수정되었습니다.");
	        } else {
	            JOptionPane.showMessageDialog(this, "수강 내역을 수정할 수 없습니다.");
	        }

	        pstmt.close();

	        viewAllMembers();

	        clearFields();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "날짜를 입력해주세요. 아직 수강중이라면 end_ymd는 null로 채워주세요.");
	        if (conn != null) {
	            try {
	                conn.rollback();
	            } catch (SQLException ex1) {
	                ex1.printStackTrace();
	            }
	        }
	    } finally {
	        if (pstmt != null) {
	            try {
	                pstmt.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if (conn != null) {
	            try {
	                conn.setAutoCommit(true);
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	
	/*수강 내역 수정 완료*/

	// 수강 내역 삭제 메서드
	private void deleteMember() {
		try {
			conn.setAutoCommit(false);

			int selectedRow = logTable.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "삭제할 튜플을 선택해주세요.");
				return;
			}

			String e_id = (String) logTable.getValueAt(selectedRow, 0);

			String deleteQuery = "DELETE FROM DB2023_ST_CLASS_LOG WHERE e_id = ?";
			pstmt = conn.prepareStatement(deleteQuery);
			pstmt.setString(1, e_id);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				conn.commit();
				JOptionPane.showMessageDialog(this, "선택한 수강 내역이 삭제되었습니다.");
			} else {
				JOptionPane.showMessageDialog(this, "선택한 수강 내역을 삭제할 수 없습니다.");
			}

			pstmt.close();

			viewAllMembers();

			clearFields();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 전체 수강 내역 조회 메서드
	private void viewAllMembers() {
		ResultSet resultSet = null;

		try {
			conn.setAutoCommit(false);

			String selectDatabaseQuery = "USE DB2023Team01;";
			Statement selectDatabaseStatement = conn.createStatement();
			selectDatabaseStatement.executeUpdate(selectDatabaseQuery);

			String query = "SELECT " + "l.e_id, " + "s.st_nm, " + "c.c_nm, " + "m.m_nm, " + "l.start_ymd, "
					+ "l.end_ymd " + "FROM " + "DB2023_ST_CLASS_LOG l "
					+ "INNER JOIN DB2023_STUDENT s ON l.st_id = s.st_id "
					+ "INNER JOIN DB2023_COURSE c ON l.c_id = c.c_id "
					+ "INNER JOIN DB2023_MEMBER m ON l.m_id = m.m_id";

			resultSet = statement.executeQuery(query);

			DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
			tableModel.setRowCount(0);

			while (resultSet.next()) {
				String e_id = resultSet.getString("e_id");
				String st_nm = resultSet.getString("st_nm");
				String c_nm = resultSet.getString("c_nm");
				String m_nm = resultSet.getString("m_nm");
				String start_ymd = resultSet.getString("start_ymd");
				String end_ymd = resultSet.getString("end_ymd");

				if (end_ymd == null) {
					end_ymd = "null";
				}

				Object[] rowData = { e_id, st_nm, c_nm, m_nm, start_ymd, end_ymd };
				tableModel.addRow(rowData);
			}

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 입력 필드 초기화 메서드
	private void clearFields() {
		st_nmField.setText("");
		c_nmField.setText("");
		m_nmField.setText("");
		start_ymdField.setText("");
		end_ymdField.setText("");
	}

	public static void main(String[] args) {
		new ClassLog();
	}
}