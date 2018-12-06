package openrtb.bidrequest.model;

/**
 * @author André Schmer
 *
 */
public enum Gender {

	/**
	 * m
	 */
	M("M"),

	/**
	 * f
	 */
	F("F"),

	/**
	 * O
	 */
	OTHER("O");

	private String value;

	Gender(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Gender convert(final String value) {
		for (final Gender gender : values()) {
			if (gender.value.equalsIgnoreCase(value)) {
				return gender;
			}
		}
		return OTHER;
	}
}
