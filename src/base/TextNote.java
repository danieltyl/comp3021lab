package base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextNote extends Note {
	String content;

	public TextNote(String title) {
		super(title);
	}

	public TextNote(String title, String content) {
		super(title);
		this.content = content;
	}

	/**
	 * load a TextNote from File f
	 *
	 * the title of the TextNote is the name of the file
	 * the content of the TextNote is the content of the file
	 *
	 * @param File f
	 */
	public TextNote(File f) {
		super(f.getName());
		this.content = getTextFromFile(f.getAbsolutePath());
	}

	/**
	 * get the content of a file
	 *
	 * @param absolutePath of the file
	 * @return the content of the file
	 */
	private String getTextFromFile(String absolutePath) {
		String result = "";

		// TODO

		try (
			FileInputStream fis = new FileInputStream(absolutePath);
			InputStreamReader in = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(in);
		) {
			result = br.readLine();
		} catch (IOException e) {
			System.out.println("TextNote::getTextFromFile() error!");
		}

		return result;
	}

	/**
	 * export text note to file
	 *
	 * @param pathFolder path of the folder where to export the note
	 * the file has to be named as the title of the note with extension ".txt"
	 *
	 * if the title contains white spaces " " they has to be replaced with underscores "_"
	 */
	public void exportTextToFile(String pathFolder) {
		if (pathFolder == "") {
			pathFolder = ".";
		}
		File file = new File(pathFolder + File.separator + this.getTitle().replaceAll(" ", "_") + ".txt");

		try (
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
		) {
			bw.write(this.content);
		} catch (IOException e) {
			System.out.println("TextNote::exportTextToFile() error!");
		}

	}
}
