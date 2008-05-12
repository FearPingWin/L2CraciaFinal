package elayne.model;

/**
 * This class simply defines the presence of players.
 * @author polbat02
 */
public class L2CharacterPresence
{
	public static final L2CharacterPresence DARK_ELF_FEMALE = new L2CharacterPresence("Dark Elf Female");

	public static final L2CharacterPresence DARK_ELF_MALE = new L2CharacterPresence("Dark Elf Male");

	public static final L2CharacterPresence DWARF_FEMALE = new L2CharacterPresence("Dwarf Female");

	public static final L2CharacterPresence DWARF_MALE = new L2CharacterPresence("Dwarf Male");

	public static final L2CharacterPresence ELF_FEMALE = new L2CharacterPresence("Elf Female");

	public static final L2CharacterPresence ELF_MALE = new L2CharacterPresence("Elf Male");

	public static final L2CharacterPresence HUMAN_FEMALE = new L2CharacterPresence("Human Female");

	public static final L2CharacterPresence HUMAN_MALE = new L2CharacterPresence("Human Male");

	public static final L2CharacterPresence MALE_ONLINE = new L2CharacterPresence("Male Online");

	public static final L2CharacterPresence ORC_FEMALE = new L2CharacterPresence("Orc Female");

	public static final L2CharacterPresence ORC_MALE = new L2CharacterPresence("Orc Male");

	private String value;

	private L2CharacterPresence(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value;
	}
}
