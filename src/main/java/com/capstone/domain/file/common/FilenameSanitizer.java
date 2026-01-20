package com.capstone.domain.file.common;

public class FilenameSanitizer {
	private static final int MAX_LENGTH = 100;

	public static String sanitize(String originalFilename) {
		if (originalFilename == null || originalFilename.isBlank()) {
			return "file";
		}

		String filename = originalFilename.replace("\\", "/");
		filename = filename.substring(filename.lastIndexOf("/") + 1);

		filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");

		filename = filename.replaceAll("_+", "_");

		if (filename.length() > MAX_LENGTH) {
			int dotIndex = filename.lastIndexOf(".");
			if (dotIndex > 0) {
				String ext = filename.substring(dotIndex);
				filename = filename.substring(0, MAX_LENGTH - ext.length()) + ext;
			} else {
				filename = filename.substring(0, MAX_LENGTH);
			}
		}

		return filename.isBlank() ? "file" : filename;
	}

}
