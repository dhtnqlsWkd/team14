package Main;

//originally uploaded to http://blog.naver.com/azure0777

import java.awt.*;
import Database.*;
import JfreeChart.JChart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.*;

class CalendarDataManager { // 6*7�迭�� ��Ÿ�� �޷� ���� ���ϴ� class
	static final int CAL_WIDTH = 7;
	final static int CAL_HEIGHT = 6;
	int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH];
	int calYear;
	int calMonth;
	int calDayOfMon;
	final int calLastDateOfMonth[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	int calLastDate;
	Calendar today = Calendar.getInstance();
	Calendar cal;

	public CalendarDataManager() {
		setToday();
	}

	public void setToday() {
		calYear = today.get(Calendar.YEAR);
		calMonth = today.get(Calendar.MONTH);
		calDayOfMon = today.get(Calendar.DAY_OF_MONTH);
		makeCalData(today);
	}

	private void makeCalData(Calendar cal) {
		// 1���� ��ġ�� ������ ��¥�� ����
		int calStartingPos = (cal.get(Calendar.DAY_OF_WEEK) + 7 - (cal.get(Calendar.DAY_OF_MONTH)) % 7) % 7;
		if (calMonth == 1)
			calLastDate = calLastDateOfMonth[calMonth] + leapCheck(calYear);
		else
			calLastDate = calLastDateOfMonth[calMonth];
		// �޷� �迭 �ʱ�ȭ
		for (int i = 0; i < CAL_HEIGHT; i++) {
			for (int j = 0; j < CAL_WIDTH; j++) {
				calDates[i][j] = 0;
			}
		}
		// �޷� �迭�� �� ä���ֱ�
		for (int i = 0, num = 1, k = 0; i < CAL_HEIGHT; i++) {
			if (i == 0)
				k = calStartingPos;
			else
				k = 0;
			for (int j = k; j < CAL_WIDTH; j++) {
				if (num <= calLastDate)
					calDates[i][j] = num++;
			}
		}
	}

	private int leapCheck(int year) { // �������� Ȯ���ϴ� �Լ�
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
			return 1;
		else
			return 0;
	}

	public void moveMonth(int mon) { // ����޷� ���� n�� ���ĸ� �޾� �޷� �迭�� ����� �Լ�(1���� +12, -12�޷� �̵� ����)
		calMonth += mon;
		if (calMonth > 11)
			while (calMonth > 11) {
				calYear++;
				calMonth -= 12;
			}
		else if (calMonth < 0)
			while (calMonth < 0) {
				calYear--;
				calMonth += 12;
			}
		cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);
		makeCalData(cal);
	}
}

public class SmartBudget extends CalendarDataManager { // CalendarDataManager�� GUI + �޸��� + �ð�
	// â ������ҿ� ��ġ��
	JFrame mainFrame;
	// ImageIcon icon = new ImageIcon (
	// Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));

	JPanel budgetPanel;
	JPanel calOpPanel;
	JButton lYearBut;
	JButton lMonBut;
	JLabel curMMYYYYLab;
	JButton nMonBut;
	JButton nYearBut;
	ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons();

	JButton todayBut;
	JLabel todayLab;
	JButton synbtn;

	JPanel budgetOpPanel;
	BudgetInfoPanel bip;

	JPanel calPanel;
	JButton weekDaysName[];
	JButton dateButs[][] = new JButton[6][7];
	listenForDateButs lForDateButs = new listenForDateButs();

	JPanel infoPanel;
	JLabel infoClock;
	OptionPanel optionPanel;

	JPanel memoPanel;
	JLabel selectedDate;
	JTextArea memoArea;
	JScrollPane memoAreaSP;
	JPanel memoSubPanel;
	JButton saveBut;
	JButton delBut;
	JButton clearBut;
	ListPanel listPanel;
	ButtonActionListener btnAListener = new ButtonActionListener();

	AddDialog addDialog = new AddDialog();
	DeleteDialog deleteDialog = new DeleteDialog();
	UpdateDialog updateDialog = new UpdateDialog();

	JChart chart = new JChart();
	JDBCExam jdbc = new JDBCExam();
	JPanel frameBottomPanel;
	JLabel bottomInfo = new JLabel("Welcome to Memo Calendar!");
	// ���, �޼���
	final String WEEK_DAY_NAME[] = { "SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT" };
	final String title = "SmartBudget";
	final String SaveButMsg1 = "�� MemoData������ �����Ͽ����ϴ�.";
	final String SaveButMsg2 = "�޸� ���� �ۼ��� �ּ���.";
	final String SaveButMsg3 = "<html><font color=red>ERROR : ���� ���� ����</html>";
	final String DelButMsg1 = "�޸� �����Ͽ����ϴ�.";
	final String DelButMsg2 = "�ۼ����� �ʾҰų� �̹� ������ memo�Դϴ�.";
	final String DelButMsg3 = "<html><font color=red>ERROR : ���� ���� ����</html>";
	final String ClrButMsg1 = "�Էµ� �޸� ������ϴ�.";

	Data infobuf = new Data(null, 0, 0, 0, 0, null);
	Data buf = new Data(null, 0, 0, 0, 0, null);
	Data tmpbuf = new Data(null, 0, 0, 0, 0, null);
	Data bufarr[] = new Data[10];

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SmartBudget();
			}
		});
	}

	public SmartBudget() { // ������� ������ ���ĵǾ� ����. �� �ǳ� ���̿� ���ٷ� ����

		mainFrame = new JFrame(title);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(1400, 800);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(false);
		// mainFrame.setIconImage(icon.getImage());
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");// LookAndFeel Windows ��Ÿ�� ����
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (Exception e) {
			bottomInfo.setText("ERROR : LookAndFeel setting failed");
		}

		calOpPanel = new JPanel();
		todayBut = new JButton("Today");
		todayBut.setToolTipText("Today");
		todayBut.addActionListener(lForCalOpButtons);
		todayLab = new JLabel(today.get(Calendar.MONTH) + 1 + "/" + today.get(Calendar.DAY_OF_MONTH) + "/"
				+ today.get(Calendar.YEAR));
		synbtn = new JButton("\uB3D9\uAE30\uD654");

		lYearBut = new JButton("<<");
		lYearBut.setToolTipText("Previous Year");
		lYearBut.addActionListener(lForCalOpButtons);
		lMonBut = new JButton("<");
		lMonBut.setToolTipText("Previous Month");
		lMonBut.addActionListener(lForCalOpButtons);
		curMMYYYYLab = new JLabel("<html><table width=100><tr><th><font size=5>" + ((calMonth + 1) < 10 ? "&nbsp;" : "")
				+ (calMonth + 1) + " / " + calYear + "</th></tr></table></html>");
		nMonBut = new JButton(">");
		nMonBut.setToolTipText("Next Month");
		nMonBut.addActionListener(lForCalOpButtons);
		nYearBut = new JButton(">>");
		nYearBut.setToolTipText("Next Year");
		nYearBut.addActionListener(lForCalOpButtons);

		calOpPanel.setLayout(new GridBagLayout());
		GridBagConstraints calOpGC = new GridBagConstraints();
		calOpGC.gridx = 1;
		calOpGC.gridy = 2;
		calOpGC.gridwidth = 2;
		calOpGC.gridheight = 1;
		calOpGC.weightx = 0;
		calOpGC.weighty = 0; // ����
		calOpGC.insets = new Insets(0, 5, 0, 0);
		calOpGC.anchor = GridBagConstraints.WEST;
		calOpGC.fill = GridBagConstraints.NONE;
		calOpPanel.add(todayBut, calOpGC);
		calOpGC.gridwidth = 3;
		calOpGC.gridx = 3;
		calOpGC.gridy = 2;
		calOpPanel.add(todayLab, calOpGC);
		calOpGC.gridwidth = 2;
		calOpGC.gridx = 5;
		calOpGC.gridy = 2;
		synbtn.addActionListener(btnAListener);
		calOpPanel.add(synbtn, calOpGC);
		calOpGC.anchor = GridBagConstraints.CENTER;
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 1;
		calOpGC.gridy = 1;
		calOpPanel.add(lYearBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 2;
		calOpGC.gridy = 1;
		calOpPanel.add(lMonBut, calOpGC);
		calOpGC.gridwidth = 2;
		calOpGC.gridx = 3;
		calOpGC.gridy = 1;
		calOpPanel.add(curMMYYYYLab, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 5;
		calOpGC.gridy = 1;
		calOpPanel.add(nMonBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 6;
		calOpGC.gridy = 1;
		calOpPanel.add(nYearBut, calOpGC);

		budgetOpPanel = new JPanel();
		Dimension budgetOpPanelSize = budgetOpPanel.getPreferredSize();
		budgetOpPanelSize.height = 100;
		budgetOpPanel.setPreferredSize(budgetOpPanelSize);
		bip = new BudgetInfoPanel(calYear, calMonth + 1);
		budgetOpPanel.add(bip);

		budgetPanel = new JPanel();
		budgetPanel.setLayout(new BorderLayout());
		budgetPanel.add(calOpPanel, BorderLayout.NORTH);
		budgetPanel.add(budgetOpPanel, BorderLayout.CENTER);

		calPanel = new JPanel();
		weekDaysName = new JButton[7];
		for (int i = 0; i < CAL_WIDTH; i++) {
			weekDaysName[i] = new JButton(WEEK_DAY_NAME[i]);
			weekDaysName[i].setBorderPainted(false);
			weekDaysName[i].setContentAreaFilled(false);
			weekDaysName[i].setForeground(Color.WHITE);
			if (i == 0)
				weekDaysName[i].setBackground(new Color(200, 50, 50));
			else if (i == 6)
				weekDaysName[i].setBackground(new Color(50, 100, 200));
			else
				weekDaysName[i].setBackground(new Color(150, 150, 150));
			weekDaysName[i].setOpaque(true);
			weekDaysName[i].setFocusPainted(false);
			calPanel.add(weekDaysName[i]);
		}
		for (int i = 0; i < CAL_HEIGHT; i++) {
			for (int j = 0; j < CAL_WIDTH; j++) {
				dateButs[i][j] = new JButton();
				dateButs[i][j].setBorderPainted(false);
				dateButs[i][j].setContentAreaFilled(false);
				dateButs[i][j].setBackground(Color.WHITE);
				dateButs[i][j].setOpaque(true);
				dateButs[i][j].addActionListener(lForDateButs);
				calPanel.add(dateButs[i][j]);
			}
		}
		calPanel.setLayout(new GridLayout(0, 7, 2, 2));
		calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		showCal(); // �޷��� ǥ��

		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		infoClock = new JLabel("", SwingConstants.RIGHT);
		infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// infoPanel.add(infoClock, BorderLayout.NORTH);
		selectedDate = new JLabel("<Html><font size=3>" + (today.get(Calendar.MONTH) + 1) + "/"
				+ today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR) + "&nbsp;(Today)</html>",
				SwingConstants.LEFT);
		selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		optionPanel = new OptionPanel();
		optionPanel.graph1.addActionListener(btnAListener);
		optionPanel.graph2.addActionListener(btnAListener);
		infoPanel.add(optionPanel);

		memoPanel = new JPanel();
		memoPanel.setBorder(BorderFactory.createTitledBorder("List"));
		memoArea = new JTextArea();
		memoArea.setLineWrap(true);
		memoArea.setWrapStyleWord(true);
		memoAreaSP = new JScrollPane(memoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		readMemo();

		memoSubPanel = new JPanel();
		saveBut = new JButton("Save");
		saveBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					File f = new File("MemoData");
					if (!f.isDirectory())
						f.mkdir();

					String memo = memoArea.getText();
					if (memo.length() > 0) {
						BufferedWriter out = new BufferedWriter(
								new FileWriter("MemoData/" + calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1)
										+ (calDayOfMon < 10 ? "0" : "") + calDayOfMon + ".txt"));
						String str = memoArea.getText();
						out.write(str);
						out.close();
						bottomInfo.setText(calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1)
								+ (calDayOfMon < 10 ? "0" : "") + calDayOfMon + ".txt" + SaveButMsg1);
					} else
						bottomInfo.setText(SaveButMsg2);
				} catch (IOException e) {
					bottomInfo.setText(SaveButMsg3);
				}
				showCal();
			}
		});
		delBut = new JButton("Delete");
		delBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				memoArea.setText("");
				File f = new File("MemoData/" + calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1)
						+ (calDayOfMon < 10 ? "0" : "") + calDayOfMon + ".txt");
				if (f.exists()) {
					f.delete();
					showCal();
					bottomInfo.setText(DelButMsg1);
				} else
					bottomInfo.setText(DelButMsg2);
			}
		});
		clearBut = new JButton("Clear");
		clearBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				memoArea.setText(null);
				bottomInfo.setText(ClrButMsg1);
			}
		});
		memoSubPanel.add(saveBut);
		memoSubPanel.add(delBut);
		memoSubPanel.add(clearBut);
		memoPanel.setLayout(new BorderLayout());
		listPanel = new ListPanel();
		listPanel.btnNewButton.addActionListener(btnAListener);
		listPanel.btnNewButton_1.addActionListener(btnAListener);
		listPanel.btnNewButton_2.addActionListener(btnAListener);
		memoPanel.add(listPanel);
		// memoPanel.add(selectedDate, BorderLayout.NORTH);
		// memoPanel.add(memoAreaSP,BorderLayout.CENTER);
		// memoPanel.add(memoSubPanel,BorderLayout.SOUTH);

		// calOpPanel, calPanel�� frameSubPanelWest�� ��ġ
		JPanel frameSubPanelWest = new JPanel();
		Dimension calOpPanelSize = calOpPanel.getPreferredSize();
		calOpPanelSize.height = 60; // calOpPanel height, �����ϸ� �Ʒ� �͵� �ڵ� ������
		calOpPanel.setPreferredSize(calOpPanelSize);
		// Dimension calPanelSize = calPanel.getPreferredSize(); //�޷� ũ�� ���� �� �� ��
		// calPanelSize.height += 200;
		// calPanel.setPreferredSize(calPanelSize);
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(budgetPanel, BorderLayout.NORTH); // �޷� ���� �κ�
		frameSubPanelWest.add(calPanel, BorderLayout.CENTER); // �޷�

		// infoPanel, memoPanel�� frameSubPanelEast�� ��ġ
		JPanel frameSubPanelEast = new JPanel();
		Dimension infoPanelSize = infoPanel.getPreferredSize();
		infoPanelSize.height = 150; // �޸� ���� �г� ũ��
		infoPanel.setPreferredSize(infoPanelSize);
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(infoPanel, BorderLayout.NORTH);
		frameSubPanelEast.add(memoPanel, BorderLayout.CENTER);

		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 810; // ���� ũ��
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);

		// �ڴʰ� �߰��� bottom Panel..
		// frameBottomPanel = new JPanel();
		// frameBottomPanel.add(bottomInfo);

		// frame�� ���� ��ġ
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(frameSubPanelWest, BorderLayout.WEST); // ����
		mainFrame.add(frameSubPanelEast, BorderLayout.CENTER); // ����
		// mainFrame.add(frameBottomPanel, BorderLayout.SOUTH);
		mainFrame.setVisible(true);

		focusToday(); // ���� ��¥�� focus�� �� (mainFrame.setVisible(true) ���Ŀ� ��ġ�ؾ���)

		// Thread �۵�(�ð�, bottomMsg �����ð��� ����)
		ThreadConrol threadCnl = new ThreadConrol();
		threadCnl.start();
	}

	private void focusToday() {
		if (today.get(Calendar.DAY_OF_WEEK) == 1)
			dateButs[today.get(Calendar.WEEK_OF_MONTH)][today.get(Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
		else
			dateButs[today.get(Calendar.WEEK_OF_MONTH) - 1][today.get(Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
	}

	private void readMemo() {

		try {
			File f = new File("MemoData/" + calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1)
					+ (calDayOfMon < 10 ? "0" : "") + calDayOfMon + ".txt");
			if (f.exists()) {
				BufferedReader in = new BufferedReader(
						new FileReader("MemoData/" + calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1)
								+ (calDayOfMon < 10 ? "0" : "") + calDayOfMon + ".txt"));
				String memoAreaText = new String();
				while (true) {
					String tempStr = in.readLine();
					if (tempStr == null)
						break;
					memoAreaText = memoAreaText + tempStr + System.getProperty("line.separator");
				}
				memoArea.setText(memoAreaText);
				in.close();
			} else
				memoArea.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		String tmp = Integer.toString(calYear) + "-";
		if (calMonth + 1 < 10)
			tmp += "0";
		tmp += Integer.toString(calMonth + 1) + "-";
		if (calDayOfMon < 10)
			tmp += "0";
		tmp += Integer.toString(calDayOfMon);

		buf.set_date(tmp);
		tmpbuf.set_date(tmp);

		String[] strarr = new String[10]; // ����Ʈ ����� ��
		if (listPanel != null) {
			listPanel.lblNewLabel.setText(tmp);
			for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
				bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
				strarr[i - 1] = bufarr[i - 1].get_str();
			}

			listPanel.list.setListData(strarr);
		}
	}

	private void showCal() {
		for (int i = 0; i < CAL_HEIGHT; i++) {
			for (int j = 0; j < CAL_WIDTH; j++) {
				String fontColor = "black";
				if (j == 0)
					fontColor = "red";
				else if (j == 6)
					fontColor = "blue";

				File f = new File("MemoData/" + calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1)
						+ (calDates[i][j] < 10 ? "0" : "") + calDates[i][j] + ".txt");

				if (f.exists()) {
					dateButs[i][j]
							.setText("<html><b><font color=" + fontColor + ">" + calDates[i][j] + "</font></b></html>");
				} else
					dateButs[i][j].setText("<html><font color=" + fontColor + ">" + calDates[i][j] + "</font></html>");
				dateButs[i][j].setVerticalAlignment(JButton.NORTH); // �޷� ��ư �ȿ��� ��ġ ����

				// JLabel todayMark = new JLabel("<html><font color=green>*</html>");
				int Sum1 = new JDBCExam().productSum_day(calYear, calMonth + 1, calDates[i][j], 1);
				int Sum2 = new JDBCExam().productSum_day(calYear, calMonth + 1, calDates[i][j], 0);
				dateButs[i][j].removeAll();

				JLabel a1 = new JLabel("<html><font color=green>" + Sum1 + "</font></html>");
				JLabel a2 = new JLabel("<html><font color=orange><br><br>" + Sum2 + "</font></html>");
				a1.setHorizontalAlignment(SwingConstants.CENTER);
				a2.setHorizontalAlignment(SwingConstants.CENTER);
				if (Sum1 != 0 && Sum2 != 0) {
					dateButs[i][j].add(a1);
					dateButs[i][j].add(a2);
				} else if (Sum1 == 0 && Sum2 != 0) {
					dateButs[i][j].add(a2);
				} else if (Sum1 != 0 && Sum2 == 0) {
					dateButs[i][j].add(a1);
				}

				if (calMonth == today.get(Calendar.MONTH) && calYear == today.get(Calendar.YEAR)
						&& calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)) {
					dateButs[i][j].setBackground(Color.LIGHT_GRAY);
					dateButs[i][j].setToolTipText("Today");
				}
				else {
					dateButs[i][j].setBackground(Color.white);
				}

				if (calDates[i][j] == 0)
					dateButs[i][j].setVisible(false);
				else
					dateButs[i][j].setVisible(true);
			}
		}
	}

	private class ListenForCalOpButtons implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == todayBut) {
				setToday();
				lForDateButs.actionPerformed(e);
				focusToday();
			} else if (e.getSource() == lYearBut)
				moveMonth(-12);
			else if (e.getSource() == lMonBut)
				moveMonth(-1);
			else if (e.getSource() == nMonBut)
				moveMonth(1);
			else if (e.getSource() == nYearBut)
				moveMonth(12);

			curMMYYYYLab.setText("<html><table width=100><tr><th><font size=5>" + ((calMonth + 1) < 10 ? "&nbsp;" : "")
					+ (calMonth + 1) + " / " + calYear + "</th></tr></table></html>");
			int sum = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 0)
					- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 0);
			int sum1 = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 1)
					- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 1);
			bip.lblNewLabel.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 0));
			bip.lblNewLabel_1.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 1));
			bip.lblNewLabel_3.setText("���� �ܾ� : " + sum);
			bip.lblNewLabel_2.setText("ī�� �ܾ� : " + sum1);
			showCal();
		}
	}

	private class listenForDateButs implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int k = 0, l = 0;
			for (int i = 0; i < CAL_HEIGHT; i++) {
				for (int j = 0; j < CAL_WIDTH; j++) {
					if (e.getSource() == dateButs[i][j]) {
						k = i;
						l = j;
					}
				}
			}

			if (!(k == 0 && l == 0))
				calDayOfMon = calDates[k][l]; // today��ư�� ���������� �� actionPerformed�Լ��� ����Ǳ� ������ ���� �κ�

			cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);

			String dDayString = new String();
			int dDay = ((int) ((cal.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 60 / 60 / 24));
			if (dDay == 0 && (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR))
					&& (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))
					&& (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)))
				dDayString = "Today";
			else if (dDay >= 0)
				dDayString = "D-" + (dDay + 1);
			else if (dDay < 0)
				dDayString = "D+" + (dDay) * (-1);

			selectedDate.setText("<Html><font size=3>" + (calMonth + 1) + "/" + calDayOfMon + "/" + calYear + "&nbsp;("
					+ dDayString + ")</html>");

			readMemo();
		}
	}

	private class ThreadConrol extends Thread {
		public void run() {
			boolean msgCntFlag = false;
			int num = 0;
			String curStr = new String();
			while (true) {
				try {
					today = Calendar.getInstance();
					String amPm = (today.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
					String hour;
					if (today.get(Calendar.HOUR) == 0)
						hour = "12";
					else if (today.get(Calendar.HOUR) == 12)
						hour = " 0";
					else
						hour = (today.get(Calendar.HOUR) < 10 ? " " : "") + today.get(Calendar.HOUR);
					String min = (today.get(Calendar.MINUTE) < 10 ? "0" : "") + today.get(Calendar.MINUTE);
					String sec = (today.get(Calendar.SECOND) < 10 ? "0" : "") + today.get(Calendar.SECOND);
					infoClock.setText(amPm + " " + hour + ":" + min + ":" + sec);

					sleep(1000);
					String infoStr = bottomInfo.getText();

					if (infoStr != " " && (msgCntFlag == false || curStr != infoStr)) {
						num = 5;
						msgCntFlag = true;
						curStr = infoStr;
					} else if (infoStr != " " && msgCntFlag == true) {
						if (num > 0)
							num--;
						else {
							msgCntFlag = false;
							bottomInfo.setText(" ");
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Thread:Error");
				}
			}
		}
	}

	public class ButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String s;

			s = ((JButton) e.getSource()).getText();

			if (s.equals("�߰�")) {

				if (e.getSource().equals(addDialog.okButton)) { // �߰� â�� �߰�
					if (addDialog.rdbtnNewRadioButton.isSelected()) { // ����
						buf.set_in_out(0); // 0�� ���� 1�� ����

						if (addDialog.inrdbtn[0].isSelected()) { // ����
							buf.set_card(0); // 0�� ����, 1�� ����

							if (addDialog.inrdbtn[2].isSelected()) { // ����
								buf.set_kinds(0); // 0�� ����, 1�� �뵷, 2�� ��Ÿ
								if (!addDialog.textField.getText().equals(null)) { // ������
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.inrdbtn[3].isSelected()) { // �뵷
								buf.set_kinds(1);
								if (!addDialog.textField.getText().equals(null)) { // ������
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.inrdbtn[4].isSelected()) { // ��Ÿ
								buf.set_kinds(2);
								if (!addDialog.textField.getText().equals(null)) { // ������
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							}
						} else if (addDialog.inrdbtn[1].isSelected()) { // ����
							buf.set_card(1);

							if (addDialog.inrdbtn[2].isSelected()) { // ����
								buf.set_kinds(0); // 0�� ����, 1�� �뵷, 2�� ��Ÿ
								if (!addDialog.textField.getText().equals(null)) { // ������
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.inrdbtn[3].isSelected()) { // �뵷
								buf.set_kinds(1);
								if (!addDialog.textField.getText().equals(null)) { // ������
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.inrdbtn[4].isSelected()) { // ��Ÿ
								buf.set_kinds(2);
								if (!addDialog.textField.getText().equals(null)) { // ������
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							}
						}

					}

					else if (addDialog.rdbtnNewRadioButton_1.isSelected()) { // ����
						buf.set_in_out(1);

						if (addDialog.outrdbtn[0].isSelected()) { // ����
							buf.set_card(0);

							if (addDialog.outrdbtn[3].isSelected()) { // �ĺ�
								buf.set_kinds(0);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[4].isSelected()) { // �����
								buf.set_kinds(1);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[5].isSelected()) { // ī��
								buf.set_kinds(2);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[6].isSelected()) { // ����ǰ
								buf.set_kinds(3);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[7].isSelected()) { // ��ȭ��Ȱ
								buf.set_kinds(4);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[8].isSelected()) { // ������
								buf.set_kinds(5);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							}

						} else if (addDialog.outrdbtn[1].isSelected()) { // üũī��
							buf.set_card(1);

							if (addDialog.outrdbtn[3].isSelected()) { // �ĺ�
								buf.set_kinds(0);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[4].isSelected()) { // �����
								buf.set_kinds(1);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[5].isSelected()) { // ī��
								buf.set_kinds(2);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[6].isSelected()) { // ����ǰ
								buf.set_kinds(3);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[7].isSelected()) { // ��ȭ��Ȱ
								buf.set_kinds(4);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[8].isSelected()) { // ������
								buf.set_kinds(5);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							}

						} else if (addDialog.outrdbtn[2].isSelected()) { // �ſ�ī��
							buf.set_card(2);

							if (addDialog.outrdbtn[3].isSelected()) { // �ĺ�
								buf.set_kinds(0);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[4].isSelected()) { // �����
								buf.set_kinds(1);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[5].isSelected()) { // ī��
								buf.set_kinds(2);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[6].isSelected()) { // ����ǰ
								buf.set_kinds(3);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[7].isSelected()) { // ��ȭ��Ȱ
								buf.set_kinds(4);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							} else if (addDialog.outrdbtn[8].isSelected()) { // ������
								buf.set_kinds(5);

								if (!addDialog.textField.getText().equals(null)) {
									buf.set_price(Integer.parseInt(addDialog.textField.getText()));
									addDialog.setVisible(false);
								}
							}
						}

					}
					if (!addDialog.isVisible()) {
						jdbc.productInsert(buf);

						String tmp = Integer.toString(calYear) + "-";
						if (calMonth + 1 < 10)
							tmp += "0";
						tmp += Integer.toString(calMonth + 1) + "-";
						if (calDayOfMon < 10)
							tmp += "0";
						tmp += Integer.toString(calDayOfMon);

						buf.set_date(tmp);
						tmpbuf.set_date(tmp);
						String strarr[] = new String[10];

						for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
							bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
							strarr[i - 1] = bufarr[i - 1].get_str();
						}

						int sum = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 0)
								- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 0);
						int sum1 = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 1)
								- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 1);
						bip.lblNewLabel.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 0));
						bip.lblNewLabel_1.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 1));
						bip.lblNewLabel_3.setText("���� �ܾ� : " + sum);
						bip.lblNewLabel_2.setText("ī�� �ܾ� : " + sum1);
						listPanel.list.setListData(strarr);
						showCal();
					}
				}

				else { // ���� â�� �߰�
					addDialog = new AddDialog();
					addDialog.okButton.addActionListener(btnAListener);
					addDialog.cancelButton.addActionListener(btnAListener);
					//addDialog.setLocation(600, 320);
					addDialog.setLocation(2000, 400);
					addDialog.rdbtnNewRadioButton.addItemListener(new ButtonItemListener());
					addDialog.rdbtnNewRadioButton_1.addItemListener(new ButtonItemListener());
					addDialog.setVisible(true);

					for (int i = 0; i < addDialog.outrdbtn.length; i++)
						addDialog.outrdbtn[i].setVisible(false);

					for (int i = 0; i < addDialog.inrdbtn.length; i++)
						addDialog.inrdbtn[i].setVisible(false);
				}
			} else if (s.equals("����")) {
				deleteDialog = new DeleteDialog();
				deleteDialog.okbtn.addActionListener(btnAListener);
				deleteDialog.cancelbtn.addActionListener(btnAListener);
				//deleteDialog.setLocation(800, 450);
				deleteDialog.setLocation(2200, 450);
				deleteDialog.setVisible(true);
			} else if (s.equals("����")) {
				if (e.getSource().equals(updateDialog.okButton)) {

					if (updateDialog.rdbtnNewRadioButton.isSelected()) { // ����
						tmpbuf.set_in_out(0); // 0�� ���� 1�� ����

						if (updateDialog.inrdbtn[0].isSelected()) { // ����
							tmpbuf.set_card(0); // 0�� ����, 1�� ����

							if (updateDialog.inrdbtn[2].isSelected()) { // ����
								tmpbuf.set_kinds(0); // 0�� ����, 1�� �뵷, 2�� ��Ÿ
								if (!updateDialog.textField.getText().equals(null)) { // ������
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.inrdbtn[3].isSelected()) { // �뵷
								tmpbuf.set_kinds(1);
								if (!updateDialog.textField.getText().equals(null)) { // ������
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.inrdbtn[4].isSelected()) { // ��Ÿ
								tmpbuf.set_kinds(2);
								if (!updateDialog.textField.getText().equals(null)) { // ������
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							}
						} else if (updateDialog.inrdbtn[1].isSelected()) { // ����
							tmpbuf.set_card(1);

							if (updateDialog.inrdbtn[2].isSelected()) { // ����
								tmpbuf.set_kinds(0); // 0�� ����, 1�� �뵷, 2�� ��Ÿ
								if (!updateDialog.textField.getText().equals(null)) { // ������
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.inrdbtn[3].isSelected()) { // �뵷
								tmpbuf.set_kinds(1);
								if (!updateDialog.textField.getText().equals(null)) { // ������
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.inrdbtn[4].isSelected()) { // ��Ÿ
								tmpbuf.set_kinds(2);
								if (!updateDialog.textField.getText().equals(null)) { // ������
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							}
						}

					}

					else if (updateDialog.rdbtnNewRadioButton_1.isSelected()) { // ����
						tmpbuf.set_in_out(1);

						if (updateDialog.outrdbtn[0].isSelected()) { // ����
							tmpbuf.set_card(0);

							if (updateDialog.outrdbtn[3].isSelected()) { // �ĺ�
								tmpbuf.set_kinds(0);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[4].isSelected()) { // �����
								tmpbuf.set_kinds(1);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[5].isSelected()) { // ī��
								tmpbuf.set_kinds(2);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[6].isSelected()) { // ����ǰ
								tmpbuf.set_kinds(3);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[7].isSelected()) { // ��ȭ��Ȱ
								tmpbuf.set_kinds(4);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[8].isSelected()) { // ������
								tmpbuf.set_kinds(5);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							}

						} else if (updateDialog.outrdbtn[1].isSelected()) { // üũī��
							tmpbuf.set_card(1);

							if (updateDialog.outrdbtn[3].isSelected()) { // �ĺ�
								tmpbuf.set_kinds(0);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[4].isSelected()) { // �����
								tmpbuf.set_kinds(1);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[5].isSelected()) { // ī��
								tmpbuf.set_kinds(2);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[6].isSelected()) { // ����ǰ
								tmpbuf.set_kinds(3);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[7].isSelected()) { // ��ȭ��Ȱ
								tmpbuf.set_kinds(4);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[8].isSelected()) { // ������
								tmpbuf.set_kinds(5);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							}

						} else if (updateDialog.outrdbtn[2].isSelected()) { // �ſ�ī��
							tmpbuf.set_card(2);

							if (updateDialog.outrdbtn[3].isSelected()) { // �ĺ�
								tmpbuf.set_kinds(0);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[4].isSelected()) { // �����
								tmpbuf.set_kinds(1);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[5].isSelected()) { // ī��
								tmpbuf.set_kinds(2);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[6].isSelected()) { // ����ǰ
								tmpbuf.set_kinds(3);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[7].isSelected()) { // ��ȭ��Ȱ
								tmpbuf.set_kinds(4);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							} else if (updateDialog.outrdbtn[8].isSelected()) { // ������
								tmpbuf.set_kinds(5);

								if (!updateDialog.textField.getText().equals(null)) {
									tmpbuf.set_price(Integer.parseInt(updateDialog.textField.getText()));
									updateDialog.setVisible(false);
								}
							}
						}

					}

					if (!updateDialog.isVisible()) {
						String[] strarr = new String[10];
						String tmp = Integer.toString(calYear) + "-"; // tmp�� ��¥ ��Ʈ��
						if (calMonth + 1 < 10)
							tmp += "0";
						tmp += Integer.toString(calMonth + 1) + "-";
						if (calDayOfMon < 10)
							tmp += "0";
						tmp += Integer.toString(calDayOfMon);

						for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
							bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
							strarr[i - 1] = bufarr[i - 1].get_str();

							if (listPanel.list.isSelectedIndex(i - 1)) {
								jdbc.productUpdate(bufarr[i - 1], tmpbuf);
								break;
							}
						}

						strarr = new String[10];
						for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
							bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
							strarr[i - 1] = bufarr[i - 1].get_str();
						}
						int sum = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 0)
								- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 0);
						int sum1 = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 1)
								- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 1);
						bip.lblNewLabel.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 0));
						bip.lblNewLabel_1.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 1));
						bip.lblNewLabel_3.setText("���� �ܾ� : " + sum);
						bip.lblNewLabel_2.setText("ī�� �ܾ� : " + sum1);
						listPanel.list.setListData(strarr);
						showCal();
					}

				} else {
					updateDialog = new UpdateDialog();
					updateDialog.okButton.addActionListener(btnAListener);
					updateDialog.cancelButton.addActionListener(btnAListener);
					//updateDialog.setLocation(600, 320);
					updateDialog.setLocation(2000, 400);
					updateDialog.rdbtnNewRadioButton.addItemListener(new ButtonItemListener());
					updateDialog.rdbtnNewRadioButton_1.addItemListener(new ButtonItemListener());
					updateDialog.setVisible(true);

					for (int i = 0; i < updateDialog.outrdbtn.length; i++)
						updateDialog.outrdbtn[i].setVisible(false);

					for (int i = 0; i < updateDialog.inrdbtn.length; i++)
						updateDialog.inrdbtn[i].setVisible(false);
				}
			} else if (s.equals("Ȯ��")) {
				String[] strarr = new String[10];
				String tmp = Integer.toString(calYear) + "-"; // tmp�� ��¥ ��Ʈ��
				if (calMonth + 1 < 10)
					tmp += "0";
				tmp += Integer.toString(calMonth + 1) + "-";
				if (calDayOfMon < 10)
					tmp += "0";
				tmp += Integer.toString(calDayOfMon);

				for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
					bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
					strarr[i - 1] = bufarr[i - 1].get_str();

					if (listPanel.list.isSelectedIndex(i - 1)) {
						jdbc.productDelete(bufarr[i - 1]);
						break;
					}
				}

				strarr = new String[10];
				for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
					bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
					strarr[i - 1] = bufarr[i - 1].get_str();
				}
				int sum = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 0)
						- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 0);
				int sum1 = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 1)
						- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 1);
				bip.lblNewLabel.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 0));
				bip.lblNewLabel_1.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 1));
				bip.lblNewLabel_3.setText("���� �ܾ� : " + sum);
				bip.lblNewLabel_2.setText("ī�� �ܾ� : " + sum1);
				listPanel.list.setListData(strarr);
				showCal();

				deleteDialog.setVisible(false);
			} else if (s.equals("���")) {
				if (e.getSource().equals(addDialog.cancelButton))
					addDialog.setVisible(false);
				else if (e.getSource().equals(deleteDialog.cancelbtn))
					deleteDialog.setVisible(false);
				else if (e.getSource().equals(updateDialog.cancelButton))
					updateDialog.setVisible(false);
			} else if (s.equals("���� �׷���")) {
				chart.showLineChart(calYear, calMonth + 1);
			} else if (s.equals("���� �м�")) {
				chart.showPieChart(calYear, calMonth + 1);
			}
			else if (s.equals("����ȭ")) {
				String[] strarr = new String[10];
				String tmp = Integer.toString(calYear) + "-"; // tmp�� ��¥ ��Ʈ��
				if (calMonth + 1 < 10)
					tmp += "0";
				tmp += Integer.toString(calMonth + 1) + "-";
				if (calDayOfMon < 10)
					tmp += "0";
				tmp += Integer.toString(calDayOfMon);
				
				strarr = new String[10];
				for (int i = 1; i <= jdbc.productCountOne(tmp); i++) {
					bufarr[i - 1] = jdbc.productSelectOne(tmp, i);
					strarr[i - 1] = bufarr[i - 1].get_str();
				}
				int sum = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 0)
						- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 0);
				int sum1 = jdbc.productSum_month_card(calYear, calMonth + 1, 0, 1)
						- jdbc.productSum_month_card(calYear, calMonth + 1, 1, 1);
				bip.lblNewLabel.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 0));
				bip.lblNewLabel_1.setText("���� : " + jdbc.productSum_month(calYear, calMonth + 1, 1));
				bip.lblNewLabel_3.setText("���� �ܾ� : " + sum);
				bip.lblNewLabel_2.setText("ī�� �ܾ� : " + sum1);
				listPanel.list.setListData(strarr);
				showCal();
			}

		}

	}

	public class ButtonItemListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			String s;

			s = ((JRadioButton) e.getSource()).getText();

			if (s.equals("����")) {
				for (int i = 0; i < addDialog.outrdbtn.length; i++)
					addDialog.outrdbtn[i].setVisible(false);

				for (int i = 0; i < addDialog.inrdbtn.length; i++)
					addDialog.inrdbtn[i].setVisible(true);

				for (int i = 0; i < updateDialog.outrdbtn.length; i++)
					updateDialog.outrdbtn[i].setVisible(false);

				for (int i = 0; i < updateDialog.inrdbtn.length; i++)
					updateDialog.inrdbtn[i].setVisible(true);
			} else if (s.equals("����")) {
				for (int i = 0; i < addDialog.inrdbtn.length; i++)
					addDialog.inrdbtn[i].setVisible(false);

				for (int i = 0; i < addDialog.outrdbtn.length; i++)
					addDialog.outrdbtn[i].setVisible(true);

				for (int i = 0; i < updateDialog.inrdbtn.length; i++)
					updateDialog.inrdbtn[i].setVisible(false);

				for (int i = 0; i < updateDialog.outrdbtn.length; i++)
					updateDialog.outrdbtn[i].setVisible(true);
			}
		}

	}
}