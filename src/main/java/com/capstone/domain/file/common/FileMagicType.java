package com.capstone.domain.file.common;

import java.util.Arrays;

public enum FileMagicType {

	JPEG(new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF}),
	PNG(new byte[]{(byte)0x89, 0x50, 0x4E, 0x47}),
	GIF(new byte[]{0x47, 0x49, 0x46, 0x38}),

	PDF(new byte[]{0x25, 0x50, 0x44, 0x46}), // %PDF
	DOC_XLS(new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0}),

	ZIP(new byte[]{0x50, 0x4B, 0x03, 0x04});

	private final byte[] magic;

	FileMagicType(byte[] magic) {
		this.magic = magic;
	}

	public byte[] magic() {
		return magic;
	}

	public static FileMagicType detect(byte[] fileHeader) {
		if (fileHeader == null || fileHeader.length == 0) {
			return null;
		}

		for (FileMagicType type : values()) {
			if (matches(fileHeader, type.magic)) {
				return type;
			}
		}
		return null;
	}

	private static boolean matches(byte[] fileHeader, byte[] magic) {
		if (fileHeader.length < magic.length) {
			return false;
		}
		for (int i = 0; i < magic.length; i++) {
			if (fileHeader[i] != magic[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean matchesContentType(String contentType) {
		if (contentType == null) {
			return false;
		}

		return switch (this) {
			case JPEG -> contentType.equals("image/jpeg");
			case PNG -> contentType.equals("image/png");
			case GIF -> contentType.equals("image/gif");
			case PDF -> contentType.equals("application/pdf");
			case DOC_XLS -> contentType.equals("application/msword") ||
					contentType.equals("application/vnd.ms-excel") ||
					contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
					contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			case ZIP -> false; // ZIP은 허용 안 함
		};
	}
}
