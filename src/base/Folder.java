package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Folder implements Comparable<Folder>{
	private ArrayList<Note> notes;
	private String name;

	public Folder(String name) {
		this.name = name;
		notes = new ArrayList<Note>();
	}

	public void addNote(Note note) {
		notes.add(note);
	}

	public String getName() {
		return name;
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}

	public void sortNotes() {
		Collections.sort(notes);
	}

	public List<Note> searchNotes(String keywords) {
		List<Note> result = new ArrayList<Note>();
		List<String> keywordList = new ArrayList<String>();

		// Parse the keywords string to a preferred format
		Scanner input = new Scanner(keywords.toLowerCase());
		input.useDelimiter(" ");
		while (input.hasNext()) {
			String currentKeyword = input.next();
			if (currentKeyword.equals("or")) {
				String previousKeyword = keywordList.get(keywordList.size() - 1);
				keywordList.set(keywordList.size() - 1, previousKeyword + " or " + input.next());
			} else {
				keywordList.add(currentKeyword);
			}
		}
		input.close();

		// Loop through all notes
		for (Note n : notes) {
			boolean match = true;
			// Get title
			String title = n.getTitle().toLowerCase();

			for (String keyword : keywordList) {
				// Check if this section contains "or"
				if (keyword.contains(" or ")) {
					//System.out.println("Line 50");
					String[] orSection = keyword.split(" or ");
					if (!title.contains(orSection[0]) && !title.contains(orSection[1])) {
						match = false;
						break;
					}
				} else if (!title.contains(keyword)) {	// Check if the keyword is in title
					match = false;
					break;
				}
			}


			// Check the content for TextNote if title doesn't match
			if (!match && n instanceof TextNote) {
				TextNote textNote = (TextNote) n;
				String content = textNote.content.toLowerCase();
				match = true;	// Second chance
				for (String keyword : keywordList) {
					// Check if this section contains "or"
					if (keyword.contains(" or ")) {
						String[] orSection = keyword.split(" or ");
						if (!content.contains(orSection[0]) && !content.contains(orSection[1])) {
							match = false;
							break;
						}
					} else if (!content.contains(keyword)) {
						match = false;
						break;
					}
				}
			}

			if (match) {
				result.add(n);
			}
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Folder other = (Folder) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		int nText = 0;
		int nImage = 0;

		ArrayList<Note> notes = getNotes();
		for (Note note : notes) {
			if (note instanceof TextNote) {
				nText += 1;
			} else if (note instanceof ImageNote) {
				nImage += 1;
			}
		}

		return name + ":" + nText + ":" + nImage;
	}

	@Override
	public int compareTo(Folder f) {
		if (name.equals(f.name)) {
			return 0;
		} else {
			return name.compareTo(f.name);
		}
	}
}
