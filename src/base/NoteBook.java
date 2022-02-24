package base;

import java.util.ArrayList;

public class NoteBook {
	private ArrayList<Folder> folders;

	public NoteBook() {
		folders = new ArrayList<Folder>();
	}

	public boolean createTextNote(String folderName, String title) {
		TextNote note = new TextNote(title);
		return insertNote(folderName, note);
	}

	public boolean createImageNote(String folderName, String title) {
		ImageNote note = new ImageNote(title);
		return insertNote(folderName, note);
	}

	public ArrayList<Folder> getFolders() {
		return folders;
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
}
