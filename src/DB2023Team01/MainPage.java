package DB2023Team01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage extends JFrame {
	private Member memberPage; // Member 페이지 인스턴스 변수
	private Student studentPage; // Student 페이지 인스턴스 변수
	private Course coursePage; // Course 페이지 인스턴스 변수
	private ClassLog enrollmentPage; // ClassLog 페이지 인스턴스 변수
	private Pay paymentPage; // Payment 페이지 인스턴스 변수

	public MainPage() {

		// 배경색 설정
		Color backgroundColor = new Color(0, 70, 42);
		getContentPane().setBackground(backgroundColor);

		// 이미지 경로 수정
		String imagePath = "C:/Users/doroc/Downloads/logo.png";
		ImageIcon icon = new ImageIcon(imagePath);
		JLabel label = new JLabel(icon);

		// 이미지 크기 조정
		int width = 200; // 원하는 너비
		int height = 200; // 원하는 높이
		Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
		ImageIcon resizedIcon = new ImageIcon(image);
		label.setIcon(resizedIcon);

		// 패널 생성 및 이미지 패널에 이미지 추가
		JPanel imagePanel = new JPanel();
		imagePanel.setBackground(backgroundColor);
		imagePanel.add(label);

		// 버튼 생성
		JButton memberButton = new JButton("직원 정보");
		JButton studentButton = new JButton("학생 정보");
		JButton courseButton = new JButton("과목 정보");
		JButton enrollmentButton = new JButton("수강 신청");
		JButton paymentButton = new JButton("결제 정보");

		// 버튼에 툴팁 추가
		memberButton.setToolTipText("회원 정보 페이지로 이동합니다.");
		studentButton.setToolTipText("학생 정보 페이지로 이동합니다.");
		courseButton.setToolTipText("과목 정보 페이지로 이동합니다.");
		enrollmentButton.setToolTipText("수강 신청 페이지로 이동합니다.");
		paymentButton.setToolTipText("결제 정보 페이지로 이동합니다.");

		// 버튼 클릭 이벤트 리스너 등록
		studentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 이미 생성된 Student 페이지가 있는지 확인
				if (studentPage == null) {
					studentPage = new Student();
					studentPage.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							studentPage = null; // Student 페이지가 종료되면 인스턴스 변수를 null로 설정
							setVisible(true); // Main 페이지 보이기
						}
					});
				} else {
					studentPage.setVisible(true); // 이미 생성된 Student 페이지 보이기
				}
				setVisible(false); // 현재 Main 페이지 숨기기
			}
		});

		memberButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 이미 생성된 Student 페이지가 있는지 확인
				if (memberPage == null) {
					memberPage = new Member();
					memberPage.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							memberPage = null; // Student 페이지가 종료되면 인스턴스 변수를 null로 설정
							setVisible(true); // Main 페이지 보이기
						}
					});
				} else {
					memberPage.setVisible(true); // 이미 생성된 Student 페이지 보이기
				}
				setVisible(false); // 현재 Main 페이지 숨기기
			}
		});

		courseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 이미 생성된 Course 페이지가 있는지 확인
				if (coursePage == null) {
					coursePage = new Course();
					coursePage.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							coursePage = null; // Course 페이지가 종료되면 인스턴스 변수를 null로 설정
							setVisible(true); // Main 페이지 보이기
						}
					});
				} else {
					coursePage.setVisible(true); // 이미 생성된 Course 페이지 보이기
				}
				setVisible(false); // 현재 Main 페이지 숨기기
			}
		});

		enrollmentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 이미 생성된 ClassLog 페이지가 있는지 확인
				if (enrollmentPage == null) {
					enrollmentPage = new ClassLog();
					enrollmentPage.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							enrollmentPage = null; // ClassLog 페이지가 종료되면 인스턴스 변수를 null로 설정
							setVisible(true); // Main 페이지 보이기
						}
					});
				} else {
					enrollmentPage.setVisible(true); // 이미 생성된 ClassLog 페이지 보이기
				}
				setVisible(false); // 현재 Main 페이지 숨기기
			}
		});

		paymentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 이미 생성된 Pay 페이지가 있는지 확인
				if (paymentPage == null) {
					paymentPage = new Pay();
					paymentPage.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							paymentPage = null; // Pay 페이지가 종료되면 인스턴스 변수를 null로 설정
							setVisible(true); // Main 페이지 보이기
						}
					});
				} else {
					paymentPage.setVisible(true); // 이미 생성된 Pay 페이지 보이기
				}
				setVisible(false); // 현재 Main 페이지 숨기기
			}
		});

		// 수평으로 버튼을 정렬하기 위한 패널 생성
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(backgroundColor);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(memberButton);
		buttonPanel.add(studentButton);
		buttonPanel.add(courseButton);
		buttonPanel.add(enrollmentButton);
		buttonPanel.add(paymentButton);

		// 전체 컨텐트 패널 생성
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(backgroundColor);
		contentPanel.add(imagePanel, BorderLayout.NORTH);
		contentPanel.add(buttonPanel, BorderLayout.CENTER);

		// JFrame에 컨텐트 패널 추가
		setContentPane(contentPanel);

		// JFrame 설정
		setTitle("Ewha All Care Database - Main Page");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null); // 화면 중앙에 표시
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.out.println("프로그램을 시작합니다.");
				new MainPage();
			}
		});
	}
}
