/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.base.Race;
import com.l2jfree.gameserver.skills.conditions.*;
import com.l2jfree.gameserver.skills.conditions.ConditionGameTime.CheckGameTime;
import com.l2jfree.gameserver.skills.conditions.ConditionPlayerState.CheckPlayerState;
import com.l2jfree.gameserver.skills.effects.EffectTemplate;
import com.l2jfree.gameserver.skills.funcs.FuncTemplate;
import com.l2jfree.gameserver.skills.funcs.Lambda;
import com.l2jfree.gameserver.skills.funcs.LambdaCalc;
import com.l2jfree.gameserver.skills.funcs.LambdaConst;
import com.l2jfree.gameserver.skills.funcs.LambdaStats;
import com.l2jfree.gameserver.templates.L2ArmorType;
import com.l2jfree.gameserver.templates.L2Equip;
import com.l2jfree.gameserver.templates.L2WeaponType;
import com.l2jfree.gameserver.templates.StatsSet;

/**
 * @author mkizub
 */
abstract class DocumentBase
{
	static Log							_log	= LogFactory.getLog(DocumentBase.class.getName());

	private File						_file;
	protected FastMap<String, String[]>	_tables;

	DocumentBase(File pFile)
	{
		_file = pFile;
		_tables = new FastMap<String, String[]>();
	}

	Document parse()
	{
		Document doc;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			doc = factory.newDocumentBuilder().parse(_file);
		}
		catch (Exception e)
		{
			_log.fatal("Error loading file " + _file, e);
			return null;
		}
		try
		{
			parseDocument(doc);
		}
		catch (Exception e)
		{
			_log.fatal("Error in file " + _file, e);
			return null;
		}
		return doc;
	}

	protected abstract void parseDocument(Document doc);

	protected abstract StatsSet getStatsSet();

	protected abstract String getTableValue(String name);

	protected abstract String getTableValue(String name, int idx);

	protected void resetTable()
	{
		_tables = new FastMap<String, String[]>();
	}

	protected void setTable(String name, String[] table)
	{
		_tables.put(name, table);
	}

	protected void parseTemplate(Node n, Object template)
	{
		Condition condition = null;
		n = n.getFirstChild();
		if (n == null)
			return;
		if ("cond".equalsIgnoreCase(n.getNodeName()))
		{
			condition = parseCondition(n.getFirstChild(), template);
			Node msg = n.getAttributes().getNamedItem("msg");
			if (condition != null && msg != null)
				condition.setMessage(msg.getNodeValue());
			n = n.getNextSibling();
		}
		for (; n != null; n = n.getNextSibling())
		{
			if ("add".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "Add", condition);
			else if ("sub".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "Sub", condition);
			else if ("mul".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "Mul", condition);
			else if ("basemul".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "BaseMul", condition);
			else if ("div".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "Div", condition);
			else if ("set".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "Set", condition);
			else if ("enchant".equalsIgnoreCase(n.getNodeName()))
				attachFunc(n, template, "Enchant", condition);
			//else if ("skill".equalsIgnoreCase(n.getNodeName()))
				//attachSkill(n, template, condition);
			else if ("effect".equalsIgnoreCase(n.getNodeName()))
			{
				if (template instanceof EffectTemplate)
					throw new RuntimeException("Nested effects");
				attachEffect(n, template, condition);
			}
		}
	}

	protected void attachFunc(Node n, Object template, String name, Condition attachCond)
	{
		Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
		String order = n.getAttributes().getNamedItem("order").getNodeValue();
		Lambda lambda = getLambda(n, template);
		int ord = Integer.decode(getValue(order, template));
		Condition applayCond = parseCondition(n.getFirstChild(), template);

		FuncTemplate ft = new FuncTemplate(attachCond, applayCond, name, stat, ord, lambda);
		if (template instanceof L2Equip)
			((L2Equip) template).attach(ft);
		else if (template instanceof L2Skill)
			((L2Skill) template).attach(ft);
		else if (template instanceof EffectTemplate)
			((EffectTemplate) template).attach(ft);
	}

	protected void attachLambdaFunc(Node n, Object template, LambdaCalc calc)
	{
		String name = n.getNodeName();
		TextBuilder sb = new TextBuilder(name);
		sb.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		name = sb.toString();
		Lambda lambda = getLambda(n, template);

		FuncTemplate ft = new FuncTemplate(null, null, name, null, calc.funcs.length, lambda);
		calc.addFunc(ft.getFunc(new Env(), calc));
	}

	protected void attachEffect(Node n, Object template, Condition attachCond)
	{
		NamedNodeMap attrs = n.getAttributes();
		String name = attrs.getNamedItem("name").getNodeValue();
		int time = 0, count = 1;
		if (attrs.getNamedItem("count") != null)
		{
			count = Integer.decode(getValue(attrs.getNamedItem("count").getNodeValue(), template));
		}
		if (attrs.getNamedItem("time") != null)
		{
			time = Integer.decode(getValue(attrs.getNamedItem("time").getNodeValue(), template));
		}

		time *= ((L2Skill) template).getTimeMulti();
		boolean self = false;
		if (attrs.getNamedItem("self") != null)
		{
			if (Integer.decode(getValue(attrs.getNamedItem("self").getNodeValue(), template)) == 1)
				self = true;
		}
		boolean icon = true;
		if (attrs.getNamedItem("noicon") != null)
		{
			if (Integer.decode(getValue(attrs.getNamedItem("noicon").getNodeValue(), template)) == 1)
				icon = false;
		}
		Lambda lambda = getLambda(n, template);
		Condition applayCond = parseCondition(n.getFirstChild(), template);
		int abnormal = 0;
		if (attrs.getNamedItem("abnormal") != null)
		{
			String abn = attrs.getNamedItem("abnormal").getNodeValue();
			if (abn.equals("bleeding"))
				abnormal = L2Character.ABNORMAL_EFFECT_BLEEDING;
			else if (abn.equals("poison"))
				abnormal = L2Character.ABNORMAL_EFFECT_POISON;
			else if (abn.equals("redcircle"))
				abnormal = L2Character.ABNORMAL_EFFECT_REDCIRCLE;
			else if (abn.equals("ice"))
				abnormal = L2Character.ABNORMAL_EFFECT_ICE;
			else if (abn.equals("wind"))
				abnormal = L2Character.ABNORMAL_EFFECT_WIND;
			else if (abn.equals("flame"))
				abnormal = L2Character.ABNORMAL_EFFECT_FLAME;
			else if (abn.equals("stun"))
				abnormal = L2Character.ABNORMAL_EFFECT_STUN;
			else if (abn.equals("mute"))
				abnormal = L2Character.ABNORMAL_EFFECT_MUTED;
			else if (abn.equals("root"))
				abnormal = L2Character.ABNORMAL_EFFECT_ROOT;
			else if (abn.equals("bighead"))
				abnormal = L2Character.ABNORMAL_EFFECT_BIG_HEAD;
			else if (abn.equals("stealth"))
				abnormal = L2Character.ABNORMAL_EFFECT_STEALTH;
			else if (abn.equals("earthquake"))
				abnormal = L2Character.ABNORMAL_EFFECT_EARTHQUAKE;
			else if (abn.equals("invul"))
				abnormal = L2Character.ABNORMAL_EFFECT_INVULNERABLE;
		}
		float stackOrder = 0;
		String stackType = "none";
		if (attrs.getNamedItem("stackType") != null)
		{
			stackType = attrs.getNamedItem("stackType").getNodeValue().intern();
		}
		if (attrs.getNamedItem("stackOrder") != null)
		{
			stackOrder = Float.parseFloat(getValue(attrs.getNamedItem("stackOrder").getNodeValue(), template));
		}

		EffectTemplate lt = new EffectTemplate(attachCond, applayCond, name, lambda, count, time, abnormal, stackType, stackOrder, icon);
		parseTemplate(n, lt);
		if (template instanceof L2Equip)
			((L2Equip) template).attach(lt);
		else if (template instanceof L2Skill && !self)
			((L2Skill) template).attach(lt);
		else if (template instanceof L2Skill && self)
			((L2Skill) template).attachSelf(lt);
	}

	protected void attachSkill(Node n, Object template, @SuppressWarnings("unused")
	Condition attachCond)
	{
		NamedNodeMap attrs = n.getAttributes();
		int id = 0, lvl = 1;
		if (attrs.getNamedItem("id") != null)
		{
			id = Integer.decode(getValue(attrs.getNamedItem("id").getNodeValue(), template));
		}
		if (attrs.getNamedItem("lvl") != null)
		{
			lvl = Integer.decode(getValue(attrs.getNamedItem("lvl").getNodeValue(), template));
		}
		L2Skill skill = SkillTable.getInstance().getInfo(id, lvl);
		if (skill == null)
		{
			_log.error("Skill not found: " + id + " " + lvl);
			return;
		}
		if (attrs.getNamedItem("chance") != null)
		{
			skill.attach(new ConditionGameChance(Integer.decode(getValue(attrs.getNamedItem("chance").getNodeValue(), template))), true);
		}

		// Could also use sql definitions ;)
		// TODO: Move item XMLs to SQL or SQL to XMLs
		//if (template instanceof L2Weapon)
		//{
			// Seems onUse handler was never used and is replaced by item handlers
			// TODO: Remove this
			//if ((attrs.getNamedItem("onCrit") == null && attrs.getNamedItem("onCast") == null))
			//((L2Weapon) template).attach(skill); // Attach as skill triggered on use

			//if (attrs.getNamedItem("onCrit") != null)
				//((L2Weapon) template).attachOnCrit(skill); // Attach as skill triggered on critical hit
			//if (attrs.getNamedItem("onCast") != null)
				//((L2Weapon) template).attachOnCast(skill); // Attach as skill triggered on cast
		//}

		// Seems not used for etcitems
		// TODO: Remove this
		//else if (template instanceof L2Item)
		//{
		//((L2Item) template).attach(skill); // Attach as skill triggered on use
		//}
	}

	protected Condition parseCondition(Node n, Object template)
	{
		while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
			n = n.getNextSibling();
		if (n == null)
			return null;
		if ("and".equalsIgnoreCase(n.getNodeName()))
			return parseLogicAnd(n, template);
		if ("or".equalsIgnoreCase(n.getNodeName()))
			return parseLogicOr(n, template);
		if ("not".equalsIgnoreCase(n.getNodeName()))
			return parseLogicNot(n, template);
		if ("player".equalsIgnoreCase(n.getNodeName()))
			return parsePlayerCondition(n);
		if ("target".equalsIgnoreCase(n.getNodeName()))
			return parseTargetCondition(n, template);
		if ("skill".equalsIgnoreCase(n.getNodeName()))
			return parseSkillCondition(n);
		if ("using".equalsIgnoreCase(n.getNodeName()))
			return parseUsingCondition(n);
		if ("game".equalsIgnoreCase(n.getNodeName()))
			return parseGameCondition(n);
		return null;
	}

	protected Condition parseLogicAnd(Node n, Object template)
	{
		ConditionLogicAnd cond = new ConditionLogicAnd();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
				cond.add(parseCondition(n, template));
		}
		if (cond.conditions == null || cond.conditions.length == 0)
			_log.fatal("Empty <and> condition in " + _file);
		return cond;
	}

	protected Condition parseLogicOr(Node n, Object template)
	{
		ConditionLogicOr cond = new ConditionLogicOr();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
				cond.add(parseCondition(n, template));
		}
		if (cond.conditions == null || cond.conditions.length == 0)
			_log.fatal("Empty <or> condition in " + _file);
		return cond;
	}

	protected Condition parseLogicNot(Node n, Object template)
	{
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				return new ConditionLogicNot(parseCondition(n, template));
			}
		}
		_log.fatal("Empty <not> condition in " + _file);
		return null;
	}

	protected Condition parsePlayerCondition(Node n)
	{
		Condition cond = null;
		byte[] forces = new byte[2];
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			if ("skill".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionWithSkill(val));
			}
			if ("race".equalsIgnoreCase(a.getNodeName()))
			{
				Race race = Race.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerRace(race));
			}
			else if ("level".equalsIgnoreCase(a.getNodeName()))
			{
				int lvl = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerLevel(lvl));
			}
			else if ("resting".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
			}
			else if ("moving".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
			}
			else if ("running".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
			}
			else if ("behind".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.BEHIND, val));
			}
			else if ("front".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FRONT, val));
			}
			else if ("flying".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
			}
			else if ("hp".equalsIgnoreCase(a.getNodeName()))
			{
				int hp = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerHp(hp));
			}
			else if ("hprate".equalsIgnoreCase(a.getNodeName()))
			{
				double rate = Double.parseDouble(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerHpPercentage(rate));
			}
			else if ("mp".equalsIgnoreCase(a.getNodeName()))
			{
				int mp = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerMp(mp));
			}
			else if ("cp".equalsIgnoreCase(a.getNodeName()))
			{
				int cp = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerCp(cp));
			}
			else if ("battle_force".equalsIgnoreCase(a.getNodeName()))
			{
				forces[0] = Byte.decode(getValue(a.getNodeValue(), null));
			}
			else if ("spell_force".equalsIgnoreCase(a.getNodeName()))
			{
				forces[1] = Byte.decode(getValue(a.getNodeValue(), null));
			}
			else if ("weight".equalsIgnoreCase(a.getNodeName()))
			{
				int weight = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerWeight(weight));
			}
		}

		if (forces[0] + forces[1] > 0)
		{
			cond = joinAnd(cond, new ConditionForceBuff(forces));
		}

		if (cond == null)
			_log.fatal("Unrecognized <player> condition in " + _file);
		return cond;
	}

	protected Condition parseTargetCondition(Node n, Object template)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			if ("aggro".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionTargetAggro(val));
			}
			else if ("level".equalsIgnoreCase(a.getNodeName()))
			{
				int lvl = Integer.decode(getValue(a.getNodeValue(), template));
				cond = joinAnd(cond, new ConditionTargetLevel(lvl));
			}
			else if ("class_id_restriction".equalsIgnoreCase(a.getNodeName()))
			{
				FastList<Integer> array = new FastList<Integer>();
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
				while (st.hasMoreTokens())
				{
					String item = st.nextToken().trim();
					array.add(Integer.decode(getValue(item, null)));
				}
				cond = joinAnd(cond, new ConditionTargetClassIdRestriction(array));
			}
			else if ("active_effect_id".equalsIgnoreCase(a.getNodeName()))
			{
				int effect_id = Integer.decode(getValue(a.getNodeValue(), template));
				cond = joinAnd(cond, new ConditionTargetActiveEffectId(effect_id));
			}
			else if ("active_skill_id".equalsIgnoreCase(a.getNodeName()))
			{
				int skill_id = Integer.decode(getValue(a.getNodeValue(), template));
				cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id));
			}
			else if("mindistance".equalsIgnoreCase(a.getNodeName()))
			{
				int distance = Integer.decode(getValue(a.getNodeValue(),null));
				cond = joinAnd(cond, new ConditionMinDistance(distance * distance));
			}
			else if ("race_id".equalsIgnoreCase(a.getNodeName()))
			{
				ArrayList<Integer> array = new ArrayList<Integer>();
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
				while (st.hasMoreTokens())
				{
					String item = st.nextToken().trim();
					//-1 because we want to take effect for exactly race that is by -1 lower in FastList
					array.add(Integer.decode(getValue(item, null)) - 1);
				}
				cond = joinAnd(cond, new ConditionTargetRaceId(array));
			}
			else if ("undead".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionTargetUndead(val));
			}
			else if ("using".equalsIgnoreCase(a.getNodeName()))
			{
				int mask = 0;
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
				while (st.hasMoreTokens())
				{
					String item = st.nextToken().trim();
					for (L2WeaponType wt : L2WeaponType.values())
					{
						if (wt.toString().equals(item))
						{
							mask |= wt.mask();
							break;
						}
					}
					for (L2ArmorType at : L2ArmorType.values())
					{
						if (at.toString().equals(item))
						{
							mask |= at.mask();
							break;
						}
					}
				}
				cond = joinAnd(cond, new ConditionTargetUsesWeaponKind(mask));
			}
		}
		if (cond == null)
			_log.fatal("Unrecognized <target> condition in " + _file);
		return cond;
	}

	protected Condition parseSkillCondition(Node n)
	{
		NamedNodeMap attrs = n.getAttributes();
		Stats stat = Stats.valueOfXml(attrs.getNamedItem("stat").getNodeValue());
		return new ConditionSkillStats(stat);
	}

	protected Condition parseUsingCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			if ("kind".equalsIgnoreCase(a.getNodeName()))
			{
				int mask = 0;
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
				while (st.hasMoreTokens())
				{
					String item = st.nextToken().trim();
					for (L2WeaponType wt : L2WeaponType.values())
					{
						if (wt.toString().equals(item))
						{
							mask |= wt.mask();
							break;
						}
					}
					for (L2ArmorType at : L2ArmorType.values())
					{
						if (at.toString().equals(item))
						{
							mask |= at.mask();
							break;
						}
					}
				}
				cond = joinAnd(cond, new ConditionUsingItemType(mask));
			}
			else if ("skill".equalsIgnoreCase(a.getNodeName()))
			{
				int id = Integer.parseInt(a.getNodeValue());
				cond = joinAnd(cond, new ConditionUsingSkill(id));
			}
			else if ("slotitem".equalsIgnoreCase(a.getNodeName()))
			{
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
				int id = Integer.parseInt(st.nextToken().trim());
				int slot = Integer.parseInt(st.nextToken().trim());
				int enchant = 0;
				if (st.hasMoreTokens())
					enchant = Integer.parseInt(st.nextToken().trim());
				cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
			}
			else if ("weaponChange".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionChangeWeapon(val));
			}
		}
		if (cond == null)
			_log.fatal("Unrecognized <using> condition in " + _file);
		return cond;
	}

	protected Condition parseGameCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			if ("night".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
			}
			if ("chance".equalsIgnoreCase(a.getNodeName()))
			{
				int val = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionGameChance(val));
			}
		}
		if (cond == null)
			_log.fatal("Unrecognized <game> condition in " + _file);
		return cond;
	}

	protected void parseTable(Node n)
	{
		NamedNodeMap attrs = n.getAttributes();
		String name = attrs.getNamedItem("name").getNodeValue();
		if (name.charAt(0) != '#')
			throw new IllegalArgumentException("Table name must start with #");
		StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
		FastList<String> array = new FastList<String>();
		while (data.hasMoreTokens())
			array.add(data.nextToken());
		String[] res = new String[array.size()];
		int i = 0;
		for (String str : array)
		{
			res[i++] = str;
		}
		setTable(name, res);
	}

	protected void parseBeanSet(Node n, StatsSet set, Integer level)
	{
		String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
		String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
		char ch = value.length() == 0 ? ' ' : value.charAt(0);
		if (ch == '#' || ch == '-' || Character.isDigit(ch))
			set.set(name, String.valueOf(getValue(value, level)));
		else
			set.set(name, value);
	}

	protected Lambda getLambda(Node n, Object template)
	{
		Node nval = n.getAttributes().getNamedItem("val");
		if (nval != null)
		{
			String val = nval.getNodeValue();
			if (val.charAt(0) == '#')
			{ // table by level
				return new LambdaConst(Double.parseDouble(getTableValue(val)));
			}
			else if (val.charAt(0) == '$')
			{
				if (val.equalsIgnoreCase("$player_level"))
					return new LambdaStats(LambdaStats.StatsType.PLAYER_LEVEL);
				if (val.equalsIgnoreCase("$target_level"))
					return new LambdaStats(LambdaStats.StatsType.TARGET_LEVEL);
				if (val.equalsIgnoreCase("$player_max_hp"))
					return new LambdaStats(LambdaStats.StatsType.PLAYER_MAX_HP);
				if (val.equalsIgnoreCase("$player_max_mp"))
					return new LambdaStats(LambdaStats.StatsType.PLAYER_MAX_MP);
				// try to find value out of item fields
				StatsSet set = getStatsSet();
				String field = set.getString(val.substring(1));
				if (field != null)
				{
					return new LambdaConst(Double.parseDouble(getValue(field, template)));
				}
				// failed
				throw new IllegalArgumentException("Unknown value " + val);
			}
			else
			{
				return new LambdaConst(Double.parseDouble(val));
			}
		}
		LambdaCalc calc = new LambdaCalc();
		n = n.getFirstChild();
		while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
			n = n.getNextSibling();
		if (n == null || !"val".equals(n.getNodeName()))
			throw new IllegalArgumentException("Value not specified");

		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			attachLambdaFunc(n, template, calc);
		}
		return calc;
	}

	protected String getValue(String value, Object template)
	{
		// is it a table?
		if (value.charAt(0) == '#')
		{
			if (template instanceof L2Skill)
				return getTableValue(value);
			else if (template instanceof Integer)
				return getTableValue(value, ((Integer) template).intValue());
			else
				throw new IllegalStateException();
		}
		return value;
	}

	protected Condition joinAnd(Condition cond, Condition c)
	{
		if (cond == null)
			return c;
		if (cond instanceof ConditionLogicAnd)
		{
			((ConditionLogicAnd) cond).add(c);
			return cond;
		}
		ConditionLogicAnd and = new ConditionLogicAnd();
		and.add(cond);
		and.add(c);
		return and;
	}
}
