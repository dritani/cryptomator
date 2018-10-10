/*******************************************************************************
 * Copyright (c) 2017 Skymatic UG (haftungsbeschränkt).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the accompanying LICENSE file.
 *******************************************************************************/
package org.cryptomator.common.settings;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.easybind.EasyBind;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VaultSettings {

	public static final boolean DEFAULT_UNLOCK_AFTER_STARTUP = false;
	public static final boolean DEFAULT_REAVEAL_AFTER_MOUNT = true;
	public static final boolean DEFAULT_USES_CUSTOM_MOUNTPATH = false;

	private final String id;
	private final ObjectProperty<Path> path = new SimpleObjectProperty<>();
	private final StringProperty mountName = new SimpleStringProperty();
	private final StringProperty winDriveLetter = new SimpleStringProperty();
	private final BooleanProperty unlockAfterStartup = new SimpleBooleanProperty(DEFAULT_UNLOCK_AFTER_STARTUP);
	private final BooleanProperty revealAfterMount = new SimpleBooleanProperty(DEFAULT_REAVEAL_AFTER_MOUNT);
	private final BooleanProperty usesCustomMountPathLinux = new SimpleBooleanProperty(DEFAULT_USES_CUSTOM_MOUNTPATH);
	private final StringProperty customMountPathLinux = new SimpleStringProperty();
	private final BooleanProperty usesCustomMountPathMac = new SimpleBooleanProperty(DEFAULT_USES_CUSTOM_MOUNTPATH);
	private final StringProperty customMountPathMac = new SimpleStringProperty();
	private final BooleanProperty usesCustomMountPathWindows = new SimpleBooleanProperty(DEFAULT_USES_CUSTOM_MOUNTPATH);
	private final StringProperty customMountPathWindows = new SimpleStringProperty();

	public VaultSettings(String id) {
		this.id = Objects.requireNonNull(id);

		EasyBind.subscribe(path, this::deriveMountNameFromPath);
	}

	Observable[] observables() {
		return new Observable[]{path, mountName, winDriveLetter, unlockAfterStartup, revealAfterMount, usesCustomMountPathLinux, customMountPathLinux, usesCustomMountPathMac, customMountPathMac, usesCustomMountPathWindows, customMountPathWindows};
	}

	private void deriveMountNameFromPath(Path path) {
		if (path != null && StringUtils.isBlank(mountName.get())) {
			mountName.set(normalizeMountName(path.getFileName().toString()));
		}
	}

	public static VaultSettings withRandomId() {
		return new VaultSettings(generateId());
	}

	private static String generateId() {
		return asBase64String(nineBytesFrom(UUID.randomUUID()));
	}

	private static String asBase64String(byte[] bytes) {
		byte[] base64Bytes = Base64.getUrlEncoder().encode(bytes);
		return new String(base64Bytes, StandardCharsets.US_ASCII);
	}

	private static byte[] nineBytesFrom(UUID uuid) {
		ByteBuffer uuidBuffer = ByteBuffer.allocate(9);
		uuidBuffer.putLong(uuid.getMostSignificantBits());
		uuidBuffer.put((byte) (uuid.getLeastSignificantBits() & 0xFF));
		uuidBuffer.flip();
		return uuidBuffer.array();
	}

	public static String normalizeMountName(String mountName) {
		String normalizedMountName = StringUtils.stripAccents(mountName);
		StringBuilder builder = new StringBuilder();
		for (char c : normalizedMountName.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (builder.length() == 0 || builder.charAt(builder.length() - 1) != '_') {
					builder.append('_');
				}
			} else if (c < 127 && Character.isLetterOrDigit(c)) {
				builder.append(c);
			} else {
				if (builder.length() == 0 || builder.charAt(builder.length() - 1) != '_') {
					builder.append('_');
				}
			}
		}
		return builder.toString();
	}

	/* Getter/Setter */

	public String getId() {
		return id;
	}

	public ObjectProperty<Path> path() {
		return path;
	}

	public StringProperty mountName() {
		return mountName;
	}

	public StringProperty winDriveLetter() {
		return winDriveLetter;
	}

	public BooleanProperty unlockAfterStartup() {
		return unlockAfterStartup;
	}

	public BooleanProperty revealAfterMount() {
		return revealAfterMount;
	}

	public BooleanProperty usesCustomMountPathLinux() {
		return usesCustomMountPathLinux;
	}

	public StringProperty customMountPathLinux() {
		return customMountPathLinux;
	}

	public BooleanProperty usesCustomMountPathMac() {
		return usesCustomMountPathMac;
	}

	public StringProperty customMountPathMac() {
		return customMountPathMac;
	}

	public BooleanProperty usesCustomMountPathWindows() {
		return usesCustomMountPathWindows;
	}

	public StringProperty customMountPathWindows() {
		return customMountPathWindows;
	}

	/* Hashcode/Equals */

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VaultSettings && obj.getClass().equals(this.getClass())) {
			VaultSettings other = (VaultSettings) obj;
			return Objects.equals(this.id, other.id);
		} else {
			return false;
		}
	}

}
