package base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteBook implements java.io.Serializable {
	private ArrayList<Folder> folders;

	private static final long serialVersionUID = 1L;

	public NoteBook() {
		folders = new ArrayList<Folder>();
	}

	/**
	 * Constructor of an object NoteBook from an object serialization on disk
	 *
	 * @param file, the path of the file for loading the object serialization
	 */
	public NoteBook(String file) {
		try (
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fis);
		) {
			NoteBook n = (NoteBook) in.readObject();
			folders = n.getFolders();
		} catch (Exception e) {
			System.out.println("NoteBook(String) error!");
		}
	}

	public boolean createTextNote(String folderName, String title) {
		TextNote note = new TextNote(title);
		return insertNote(folderName, note);
	}

	public boolean createTextNote(String folderName, String title, String content) {
		TextNote note = new TextNote(title, content);
		return insertNote(folderName, note);
	}

	public boolean createImageNote(String folderName, String title) {
		ImageNote note = new ImageNote(title);
		return insertNote(folderName, note);
	}

	public ArrayList<Folder> getFolders() {
		return folders;
	}

	public void sortFolders() {
		// for each folder, sort its notes
		for (Folder f : folders) {
			f.sortNotes();
		}

		// Sort folders
		Collections.sort(folders);

	}

	public boolean insertNote(String folderName, Note note) {
		Folder f = null;
		// Step 1: Check if the folderName already exists
		for (Folder folder : folders) {
			if (folderName.equals(folder.getName())) {
				f = folder;
			}
		}

		if (f == null) {
			f = new Folder(folderName);
			folders.add(f);
		}

		// Step 2 : Check if the new note title is used in the folder
		for (Note n : f.getNotes()) {
			// there is duplicate
			if (n.equals(note)) {
				System.out.println("Creating note " + note.getTitle() + " under folder " + folderName + " failed");
				return false;
			}
		}

		// no duplicate
		f.addNote(note);
		return true;
	}

	public List<Note> searchNotes(String keywords) {
		List<Note> result = new ArrayList<Note>();
		for (Folder f : folders) {
			for (Note n : f.searchNotes(keywords)) {
				result.add(n);
			}
		}

		return result;
	}

	/**
	 * method to save the NoteBook instance to file
	 *
	 * @param file, the path of the file where to save the object serialization
	 * @return true if save on file is successful, false otherwise
	 */
	public boolean save(String file) {
		try (
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fos);
		) {
			out.writeObject(this);
		} catch (Exception e) {
			System.out.println("NoteBook::save() error");
			return false;
		}
		return true;
	}
}
