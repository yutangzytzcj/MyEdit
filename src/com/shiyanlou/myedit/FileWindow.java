package com.shiyanlou.myedit;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
// 压制信息，不会的同学可以不理会。
public class FileWindow extends JFrame implements ActionListener, Runnable {

	/*
	 * 注意：因为实现了ActionListener
	 * 和Runnable接口，所以必须要实现这两个接口的方法。这里我们先把这两个方法简单实现以下。下节课将彻底完成这两个方法。
	 */

	Thread compiler = null;
	Thread run_prom = null;
	boolean bn = true;
	CardLayout mycard; // 声明布局，以后会用到
	File file_saved = null;
	JButton button_input_txt, // 按钮的定义
			button_compiler_text, button_compiler,
			button_run_prom,
			button_see_doswin;

	JPanel p = new JPanel();
	JTextArea input_text = new JTextArea(); // 程序输入区
	JTextArea compiler_text = new JTextArea();// 编译错误显示区
	JTextArea dos_out_text = new JTextArea();// 程序的输出信息

	JTextField input_file_name_text = new JTextField();
	JTextField run_file_name_text = new JTextField();

	public FileWindow() {
		// TODO Auto-generated constructor stub
		super("Java语言编译器");
		mycard = new CardLayout();
		compiler = new Thread(this);
		run_prom = new Thread(this);
		button_input_txt = new JButton("程序输入区（白色）");
		button_compiler_text = new JButton("编译结果区（粉红色）");
		button_see_doswin = new JButton("程序运行结果（浅蓝色）");
		button_compiler = new JButton("编译程序");
		button_run_prom = new JButton("运行程序");

		p.setLayout(mycard);// 设置卡片布局
		p.add("input", input_text);// 定义卡片名称
		p.add("compiler", compiler_text);
		p.add("dos", dos_out_text);
		add(p, "Center");

		compiler_text.setBackground(Color.pink); // 设置颜色
		dos_out_text.setBackground(Color.cyan);
		JPanel p1 = new JPanel();

		p1.setLayout(new GridLayout(3, 3)); // 设置表格布局
		// 添加组件
		p1.add(button_input_txt);
		p1.add(button_compiler_text);
		p1.add(button_see_doswin);
		p1.add(new JLabel("输入编译文件名（.java）："));
		p1.add(input_file_name_text);
		p1.add(button_compiler);
		p1.add(new JLabel("输入应用程序主类名"));
		p1.add(run_file_name_text);
		p1.add(button_run_prom);
		add(p1, "North");

		// 定义事件
		button_input_txt.addActionListener(this);
		button_compiler.addActionListener(this);
		button_compiler_text.addActionListener(this);
		button_run_prom.addActionListener(this);
		button_see_doswin.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_input_txt) {
			mycard.show(p, "input");
		} else if (e.getSource() == button_compiler_text) {
			mycard.show(p, "compiler");
		} else if (e.getSource() == button_see_doswin) {
			mycard.show(p, "dos");
		} else if (e.getSource() == button_compiler) {
			if (!(compiler.isAlive())) {
				compiler = new Thread(this);
			}
			try {
				compiler.start();

			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

			mycard.show(p, "compiler");

		} else if (e.getSource() == button_run_prom) {
			if (!(run_prom.isAlive())) {
				run_prom = new Thread(this);
			}
			try {
				run_prom.start();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			mycard.show(p, "dos");
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Thread.currentThread() == compiler) {
			compiler_text.setText(null);
			String temp = input_text.getText().trim();
			byte[] buffer = temp.getBytes();
			int b = buffer.length;
			String file_name = null;
			file_name = input_file_name_text.getText().trim();

			try {
				file_saved = new File(file_name);
				FileOutputStream writefile = null;
				writefile = new FileOutputStream(file_saved);
				writefile.write(buffer, 0, b);
				writefile.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("ERROR");
			}
			try {

				// 获得该进程的错误流，才可以知道运行结果到底是失败了还是成功。
				Runtime rt = Runtime.getRuntime();
				InputStream in = rt.exec("javac " + file_name).getErrorStream(); // 通过Runtime调用javac命令。注意：“javac
																					// ”这个字符串是有一个空格的！！

				BufferedInputStream bufIn = new BufferedInputStream(in);

				byte[] shuzu = new byte[100];
				int n = 0;
				boolean flag = true;

				// 输入错误信息
				while ((n = bufIn.read(shuzu, 0, shuzu.length)) != -1) {
					String s = null;
					s = new String(shuzu, 0, n);
					compiler_text.append(s);
					if (s != null) {
						flag = false;
					}
				}
				// 判断是否编译成功
				if (flag) {
					compiler_text.append("Compile Succeed!");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (Thread.currentThread() == run_prom) {
			// 运行文件，并将结果输出到dos_out_text

			dos_out_text.setText(null);

			try {
				Runtime rt = Runtime.getRuntime();
				String path = run_file_name_text.getText().trim();
				Process stream = rt.exec("java " + path);// 调用java命令

				InputStream in = stream.getInputStream();
				BufferedInputStream bisErr = new BufferedInputStream(
						stream.getErrorStream());
				BufferedInputStream bisIn = new BufferedInputStream(in);

				byte[] buf = new byte[150];
				byte[] err_buf = new byte[150];

				@SuppressWarnings("unused")
				int m = 0;
				@SuppressWarnings("unused")
				int i = 0;
				String s = null;
				String err = null;

				// 打印编译信息及错误信息
				while ((m = bisIn.read(buf, 0, 150)) != -1) {
					s = new String(buf, 0, 150);
					dos_out_text.append(s);
				}
				while ((i = bisErr.read(err_buf)) != -1) {
					err = new String(err_buf, 0, 150);
					dos_out_text.append(err);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
