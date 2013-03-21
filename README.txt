ECE 150 Mobile Development: Final Project
Winter 2013 Professor Tim Cheng

Written By:
Abraham Eliares Dela Cruz
Raj Luhar

AppName: bookStored

3rd Party: Tess-two library, provides basic tools to use OCR.

How to build program: Requires already having the latest Android NDK version. Download "tess-two", 
				      which is a basic working OCR tool from https://github.com/rmtheis/tess-two. 
				      Once the files have been downloaded, simply execute the "ndk-build" command 
				      within the tess-two folder to initiate the conversion of the C-relative code. 
				      Once the build has completed, import the library to the bookStored dependencies 
				      under the android properties of bookStored and execute the app.
	
Functionality: 
				-When first started, the app prompts with the logo "bookStored" as well as a button to take photos 
				and a button to take view notes. 
				-When taking a photo, the "take photo" button calls the phone's default camera to capture an image and once the 
				picture is taken, the user confirms they would like to use the given image to convert into a string.
				The OCRed text is then saved into a editable textfile.
				-When looking at notes, the "View Notes" button will bring up all notes located in the bookStored folder
				and display them in a clickable format, where the title, authorname, and page number appear. Once a note 
				is clicked, it brings up the "noteView" activity where the user is able to read the content of the .txt 
				file. A "edit" button is located at the bottom of the noteView as well which contains various fields
				which change the settings of the text file. 
				-After saving changes to a text file, the changes are readily displayed on the ListView of the items and 
				the user may go back to the main menu to repeat the cycle once more. 
				
Limitations:
				The OCR engine is light sensitive and performs best when under ideal lighting conditions. It is best to take
				photos that are on actual paper rather than displayed on a screen. We found that the very pixels of the computer
				screen were enough to interfere with the results of the OCR engine. Large groups of texts also propose a 
				problem for our app because it overloads the buffer of the OCR, causing the app to crash close. Although it 
				may occasionally work, we were unable to determine what the define factor was to measure the size of 
				text which the app could comfortably convert.
