package base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import base.Folder;
import base.Note;
import base.NoteBook;
import base.TextNote;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * NoteBook GUI with JAVAFX
 *
 * COMP 3021
 *
 *
 * @author valerio
 *
 */
public class NoteBookWindow extends Application {

	/**
	 * TextArea containing the note
	 */
	final TextArea textAreaNote = new TextArea("");

	/**
	 * list view showing the titles of the current folder
	 */
	final ListView<String> titleslistView = new ListView<String>();

	/**
	 *
	 * Combobox for selecting the folder
	 *
	 */
	final ComboBox<String> foldersComboBox = new ComboBox<String>();

	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;

	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";

	/**
	 * current search string
	 */
	String currentSearch = "";

	/**
	 * current note selected by the user
	 */
	String currentNote = "";

	/**
	 * the stage of this display
	 */
	Stage stage;

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		loadNoteBook();
		this.stage = stage;
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
	}

	/**
	 * This create the top section
	 *
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load from File");
		buttonLoad.setPrefSize(100, 20);
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Lab 8 Task 1
				// Load a NoteBook from file
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose An File Which Contains a NoteBook Object!");

				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);

				File file = fileChooser.showOpenDialog(stage);

				if (file != null) {
					loadNoteBook(file);
				}
			}
		});

		Button buttonSave = new Button("Save to File");
		buttonSave.setPrefSize(100, 20);
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Lab 8 Task 1
				// Save a NoteBook to file
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose An File Which Contains a NoteBook Object!");

				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);

				File file = fileChooser.showOpenDialog(stage);

				if (file != null) {
					noteBook.save(file.getAbsolutePath());

					showAlert(AlertType.INFORMATION,
							"Successfully saved",
							"Your file has been saved to file " + file.getName())
					.ifPresent(rs -> {
						if (rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						}
					});
				}
			}
		});

		// Lab 7 Task 5: Search field
		// Search label
		Label labelSearch = new Label("Search: ");
		// TextField
		TextField textSearch = new TextField();
		// Search button
		Button buttonSearch = new Button("Search");
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentSearch = textSearch.getText();
				textAreaNote.setText("");
				updateListView();
			}
		});

		// Clear search button
		Button buttonClear = new Button("Clear Search");
		buttonClear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentSearch = "";
				textSearch.setText("");
				textAreaNote.setText("");
				updateListView();
			}
		});

		hbox.getChildren().addAll(buttonLoad, buttonSave, labelSearch, textSearch, buttonSearch, buttonClear);

		return hbox;
	}

	/**
	 * this create the section on the left
	 *
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes

		HBox hbox = new HBox();
		hbox.setSpacing(8);

		// Lab 7 Task 2: Populate the foldersComboBox
		for (Folder f : noteBook.getFolders()) {
			foldersComboBox.getItems().add(f.getName());
		}

		setupFolderListener();

		foldersComboBox.setValue("-----");

		Button buttonAddFolder = new Button("Add a Folder");
		buttonAddFolder.setPrefSize(100, 20);
		buttonAddFolder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog("Add a Folder");
				dialog.setTitle("Input");;
				dialog.setHeaderText("Add a new folder for your notebook:");
				dialog.setContentText("Please enter the name you want to create:");

				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					// Lab 8 Task 2
					String inputName = result.get();

					// Validation
					if (inputName.equals("")) {
						showAlert(AlertType.WARNING, "Warning", "Please input an valid folder name");
						return;
					} else {
						for (Folder f : noteBook.getFolders()) {
							if (f.getName().equals(inputName)) {
								showAlert(AlertType.WARNING,
										"Warning",
										"You already have a folder named with " + inputName);
								return;
							}
						}
					}

					noteBook.addFolder(inputName);	// Add the folder
					foldersComboBox.getItems().add(inputName);		// Update folder list
				}
			}
		});

		// Add the node to hbox
		hbox.getChildren().add(foldersComboBox);
		hbox.getChildren().add(buttonAddFolder);


		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				currentNote = title;
				// This is the selected title
				// Lab 7 Task 4: load the content of the selected note in
				// textAreNote

				// Get the current folder obj
				Folder folderObj = null;
				for (Folder f : noteBook.getFolders()) {
					if (f.getName().equals(currentFolder)) {
						folderObj = f;
					}
				}

				// Get the selected note
				TextNote textnote = null;
				for (Note n : folderObj.getNotes()) {
					if (n instanceof TextNote && n.getTitle().equals(title)) {
						textnote = (TextNote) n;
					}
				}

				String content = textnote.content;
				textAreaNote.setText(content);
			}
		});

		Button buttonAddNote = new Button("Add a Note");
		buttonAddNote.setPrefSize(100, 20);
		buttonAddNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if user selected a folder
				if (currentFolder.equals("") || currentFolder.equals("-----")) {
					showAlert(AlertType.WARNING, "Warning", "Please choose a folder first!");
					return;
				}

				TextInputDialog dialog = new TextInputDialog("Add a Note");
				dialog.setTitle("Input");
				dialog.setHeaderText("Add a new note to current folder");
				dialog.setContentText("Please enter the name of your note:");

				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					String inputNoteName = result.get();

					// Create the textNote and show popup if success
					if (noteBook.createTextNote(currentFolder, inputNoteName)) {
						showAlert(AlertType.INFORMATION,
								"Successful!",
								"Insert note " + inputNoteName + " to folder " + currentFolder + " successfully!");

					}

					updateListView();
				}
			}
		});

		vbox.getChildren().add(new Label("Choose folder: "));
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);
		vbox.getChildren().add(buttonAddNote);

		return vbox;
	}

	private void setupFolderListener() {
		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 != null) {
					currentFolder = t1.toString();
				} else {
					currentFolder = "";
				}

				// this contains the name of the folder selected
				// Lab 7 Task 2: update listview
				updateListView();
			}
		});
	}

	private void updateListView() {
		ArrayList<String> list = new ArrayList<String>();

		// Lab 7 Task 3: populate the list object with all the TextNote titles of the
		// currentFolder

		// Get the current folder obj
		Folder folderObj = null;
		for (Folder f : noteBook.getFolders()) {
			if (f.getName().equals(currentFolder)) {
				folderObj = f;
			}
		}

		// Get titles from folders
		if (folderObj != null) {
			List<Note> listOfNote = null;
			// Get a list of notes based on search string
			if (currentSearch.equals("")) {
				listOfNote = folderObj.getNotes();
			} else {
				listOfNote = folderObj.searchNotes(currentSearch);
			}

			// Retrieve the title
			for (Note n : listOfNote) {
				if (n instanceof TextNote) {
					list.add(n.getTitle());
				}
			}
		}

		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		HBox hbox = new HBox();
		hbox.setSpacing(10); // Gap between nodes

		ImageView saveView = new ImageView(new Image(new File("save.png").toURI().toString()));
		saveView.setFitHeight(18);
		saveView.setFitWidth(18);
		saveView.setPreserveRatio(true);

		Button buttonSaveNote = new Button("Save Note");
		buttonSaveNote.setPrefSize(100, 20);
		buttonSaveNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if a folder and a note have been selected
				if (currentFolder.equals("") || currentFolder.equals("-----") || currentNote.equals("")) {
					showAlert(AlertType.WARNING, "Warning", "Please select a folder and a note");
					return;
				}

				String content = textAreaNote.getText().toString();

				Folder folderObj = null;
				for (Folder f : noteBook.getFolders()) {
					// Find the selected folder object
					if (f.getName().equals(currentFolder))
						folderObj = f;

					// Change the selected note
					if (folderObj != null) {
						for (Note n : folderObj.getNotes()) {
							if (n.getTitle().equals(currentNote) && n instanceof TextNote) {
								TextNote tn = (TextNote) n;
								tn.content = content;
							}
						}
					}
				}
			}
		});

		ImageView deleteView = new ImageView(new Image(new File("delete.png").toURI().toString()));
		deleteView.setFitHeight(18);
		deleteView.setFitWidth(18);
		deleteView.setPreserveRatio(true);

		Button buttonDeleteNote = new Button("Delete Note");
		buttonDeleteNote.setPrefSize(100, 20);
		buttonDeleteNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if a folder and a note have been selected
				if (currentFolder.equals("") || currentFolder.equals("-----") || currentNote.equals("")) {
					showAlert(AlertType.WARNING, "Warning", "Please select a folder and a note");
					return;
				}

				Folder folderObj = null;

				for (Folder f : noteBook.getFolders()) {
					if (f.getName().equals(currentFolder))
						folderObj = f;

					if (folderObj != null) {
						if (folderObj.removeNotes(currentNote)) {
							// Show a confirmation message
							showAlert(AlertType.CONFIRMATION, "Succeed!", "Your note has been successfully removed");
							updateListView();
						}
					}
				}
			}
		});

		hbox.getChildren().addAll(saveView, buttonSaveNote, deleteView, buttonDeleteNote);
		grid.add(hbox, 0, 0);

		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		// 0 0 is the position in the grid
		grid.add(textAreaNote, 0, 1);

		return grid;
	}

	private Optional showAlert(AlertType alertType, String Title, String Content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(Title);
		alert.setContentText(Content);
		return alert.showAndWait();
	}

	// Lab 8 Task 1
	private void loadNoteBook(File file) {
		NoteBook nb = new NoteBook(file.getAbsolutePath());
		noteBook = nb;

		// Update folder list
		foldersComboBox.valueProperty().set(null);
		foldersComboBox.getItems().clear();
		for (Folder f : noteBook.getFolders()) {
			foldersComboBox.getItems().add(f.getName());
		}
		foldersComboBox.setValue("-----");
	}

	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called “the most shocking play in NFL history” and the Washington Redskins dubbed the “Throwback Special”: the November 1985 play in which the Redskins’ Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Award–winning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everything—until it wasn’t. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliant—a part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwether’s Daddy Was a Number Runner and Dorothy Allison’s Bastard Out of Carolina, Jacqueline Woodson’s Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthood—the promise and peril of growing up—and exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;

	}

}
