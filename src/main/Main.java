package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.awt.event.ActionEvent;

public class Main {

	private JFrame frmmusicrewind;
	private JTextField textFieldPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmmusicrewind.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frmmusicrewind = new JFrame();
		frmmusicrewind.setResizable(false);
		frmmusicrewind.setTitle("MusicRewind");
		frmmusicrewind.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		frmmusicrewind.setBounds(100, 100, 340, 260);
		frmmusicrewind.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frmmusicrewind.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		frmmusicrewind.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblNumberOfLines = new JLabel("Number of lines:");
		lblNumberOfLines.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblNumberOfLines.setBounds(12, 51, 97, 16);
		panel.add(lblNumberOfLines);
		
		JSpinner spinnerLines = new JSpinner();
		spinnerLines.setModel(new SpinnerNumberModel(new Integer(10), new Integer(0), new Integer(Integer.MAX_VALUE), new Integer(1)));
		spinnerLines.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		spinnerLines.setBounds(121, 48, 96, 22);
		panel.add(spinnerLines);
		
		JLabel lblYear = new JLabel("Year:");
		lblYear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblYear.setBounds(12, 16, 29, 16);
		panel.add(lblYear);
		
		JSpinner spinnerYear = new JSpinner();
		spinnerYear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		spinnerYear.setModel(new SpinnerNumberModel(new Long(Calendar.getInstance().get(Calendar.YEAR)), new Long(0), new Long(Long.MAX_VALUE), new Long(1)));
		spinnerYear.setBounds(121, 13, 96, 22);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerYear, "#");
		spinnerYear.setEditor(editor);
		panel.add(spinnerYear);
		
		JCheckBox chkboxShowTotals = new JCheckBox("Show totals");
		chkboxShowTotals.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		chkboxShowTotals.setBounds(12, 79, 113, 25);
		panel.add(chkboxShowTotals);
		
		JLabel lblPathToHistory = new JLabel("Path to history JSON file:");
		lblPathToHistory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblPathToHistory.setBounds(12, 113, 144, 16);
		panel.add(lblPathToHistory);
		
		textFieldPath = new JTextField();
		textFieldPath.setText("watch-history.json");
		textFieldPath.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		textFieldPath.setBounds(12, 142, 205, 22);
		panel.add(textFieldPath);
		textFieldPath.setColumns(10);
		
		JButton btnOpen = new JButton("Open...");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int result = fileChooser.showOpenDialog(frmmusicrewind);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					textFieldPath.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		btnOpen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnOpen.setBounds(229, 141, 75, 25);
		panel.add(btnOpen);
		
		JButton btnRewind = new JButton("Rewind!");
		btnRewind.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnRewind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String error = rewind((Long)spinnerYear.getValue(), (Integer)spinnerLines.getValue(), chkboxShowTotals.isSelected(), textFieldPath.getText());
				if(error != null) {
					JOptionPane.showMessageDialog(frmmusicrewind, error, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnRewind.setBounds(12, 177, 97, 25);
		panel.add(btnRewind);
	}
	
	//year as long? why, yes, I do foresee this tool being used past the year 2147483647.
	public String rewind(long year, int lines, boolean showTotals, String filename) {
		
		//load and parse history JSON file
		JSONArray jsonArray = null;
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			jsonArray = new JSONArray(content);
		} catch(Exception e) {
			return "ERROR: Something went wrong while loading the history JSON file:\n" + e.toString();
		}
		
		ArrayList<Artist> artists = new ArrayList<Artist>();
		ArrayList<Song> songs = new ArrayList<Song>();
		//this ended up not being used
		//long totalSongs = 0;
		
		//process the JSON array
		for(int i=0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String header = jsonObject.getString("header");
			//process only "videos" "watched" through YouTube Music and not regular YouTube.
			if(header.equals("YouTube Music")) {
				String time = jsonObject.getString("time");
				//process only entries from the selected year
				if(time.startsWith(year + "-")) {
					String title = jsonObject.getString("title");
					//process only "watched" "videos" and not any other activity, as the history includes some other stuff
					if(title.startsWith("Watched ")) {
						try {
							//get rid of the "Watched" part of the title to get the actual name of the song
							String song = title.replace("Watched ", "");
							
							//get the artist name, similarly getting rid of the " - Topic" part of the subtitles.name field
							JSONObject subtitles = jsonObject.getJSONArray("subtitles").getJSONObject(0);
							String artist = subtitles.getString("name").replace(" - Topic", "");
							
							//ignore entries where subtitles.name is "Music Library Uploads" (i.e., songs uploaded by the user)
							//in these cases we just can't get the artist's name, but song name should be fine
							if(!artist.equals("Music Library Uploads")) {
								//add artist to list or increment count if it's already in it
								Artist a = new Artist(artist);
								if(artists.contains(a)) {
									artists.get(artists.indexOf(a)).increment();
								}
								else {
									artists.add(a);
								}
							}
							
							//add song to list or increment count if it's already in it
							Song s = new Song(song);
							if(songs.contains(s)) {
								songs.get(songs.indexOf(s)).increment();
							}
							else {
								songs.add(s);
							}
							//totalSongs++;
						} catch(Exception e) {
							//JSON entry doesn't have song or artist name so we'll just quietly skip it
						}
					}
				}
			}
		}
		
		//sort lists in decreasing order
		Collections.sort(artists, new Comparator<Artist>() {
			public int compare(Artist left, Artist right) {
			return right.getCount() - left.getCount();
			}
		});
		
		Collections.sort(songs, new Comparator<Song>() {
			public int compare(Song left, Song right) {
				return right.getCount() - left.getCount();
			}
		});
		
		//generate HTML code
		//this block generates all the CSS and the table headers
		String out = "<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>#YTMusicRewind</title>\n" +
		"<style type=\"text/css\">\n" +
		".tg {border-collapse:collapse;border-spacing:0;background-color:#212121;color:white;}\n" +
		".tg td{border-style:hidden;font-family:Segoe UI, Helvetica, Arial, sans-serif;font-size:14px;" +
		"font-weight:bold;overflow:hidden;padding:5px 50px;word-break:normal;}\n" +
		".tg th{border-style:hidden;font-family:Segoe UI, Helvetica, Arial, sans-serif;font-size:14px;" +
		"font-weight:bold;overflow:hidden;padding:5px 50px;word-break:normal;}\n" +
		".tg .tg-0lax{text-align:left;vertical-align:top}\n" +
		".tg-footer-left{color:black;background-color:red;text-align:left;vertical-align:top;}\n" +
		".tg-footer-right{color:black;background-color:red;text-align:right;vertical-align:top;}\n" +
		".red{color:red;}\n" +
		".gray{color:gray; font-size:10px;}\n" +
		"</style>\n" +
		"</head>\n<body>\n" + 
		"<table class=\"tg\">\n" +
		"<thead>\n" +
		"<tr>\n";
		out += "<th class=\"tg-0lax\">TOP ARTISTS";
		if(showTotals) {
			out += " <span class = \"gray\">(" + artists.size() + ")</span>";
		}
		out += "</th>" + 
		"<th class=\"tg-0lax\">TOP SONGS";
		if(showTotals) {
			out += " <span class = \"gray\">(" + songs.size() + ")</span>";
		}
		out += "</th>" + 
		"</tr>" +
		"</thead>" +
		"<tbody>";
		
		//table body (i.e. the actual top songs/artists)
		for(int i=0; i<lines; i++) {
			String num = "" + (i+1);
			//if n >= 10, add a leading zero to single digit numbers to pad things out nicely
			//(the user may select a very high number like 100+ but in that case they're on their own)
			if(lines >= 10 && num.length()==1) {
				num = "0" + num;
			}
			
			try {
				String artist = artists.get(i).getName();
				String song = songs.get(i).getName();
				
				out += "<tr>\n";
				out += "<td class=\"tg-0lax\"><span class=\"red\">" + num + "</span> " + artist;
				if(showTotals) {
					out += " <span class = \"gray\">(" + artists.get(i).getCount() + ")</span>";
				}
				out += "</td>\n";
				out += "<td class=\"tg-0lax\"><span class=\"red\">" + num + "</span> " + song;
				if(showTotals) {
					out += " <span class = \"gray\">(" + songs.get(i).getCount() + ")</span>";
				}
				out += "</td>\n";
				out += "</tr>\n";
			} catch(Exception e) {
				//if there aren't enough artists or songs in the user's history to fill all n lines we'll just skip the problematic lines
			}
		}
		
		//a nice little footer
		out += "<tr>\n"
				+ "<td class=\"tg-footer-left\">github.com/pyorox/MusicRewind</td>\n"
				+ "<td class=\"tg-footer-right\">&#35;YTMusicRewind</td>\n"
				+ "</tr>\n";
		out += "</tbody>\n</table>\n</body>\n</html>";
		
		//write the generated HTML to a file and open it in the user's default browser
		try {
			File f = new File("MusicRewind.html");
			PrintWriter p = new PrintWriter(f, "UTF-8");
			p.print(out);
			p.close();
			Desktop.getDesktop().open(f);
		} catch (IOException e) {
			return "ERROR: Something went wrong while writing the output file:\n" + e.toString();
		}
		
		return null;
	}
}
