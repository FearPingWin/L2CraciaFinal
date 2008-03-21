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
package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.model.Inventory;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Decoy;
import net.sf.l2j.gameserver.model.L2Summon;
import net.sf.l2j.gameserver.model.L2Transformation;
import net.sf.l2j.gameserver.model.L2Trap;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
/**
 * This class ...
 * 
 * @version $Revision: 1.7.2.4.2.9 $ $Date: 2005/04/11 10:05:54 $
 */
public class NpcInfo extends L2GameServerPacket
{
    //   ddddddddddddddddddffffdddcccccSSddd dddddc
    //   ddddddddddddddddddffffdddcccccSSddd dddddccffd
         
         
    private static final String _S__22_NPCINFO = "[S] 16 NpcInfo";
    private L2Character _activeChar;
    private int _x, _y, _z, _heading;
    private int _idTemplate;
    private boolean _isAttackable, _isSummoned;
    private int _mAtkSpd, _pAtkSpd;
    private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
    private int _rhand, _lhand;
    private int _collisionHeight, _collisionRadius;
    private String _name = "";
    private String _title = "";

    /**
     * @param _activeCharracters
     */
    public NpcInfo(L2NpcInstance cha, L2Character attacker)
    {
        _activeChar = cha;
        _idTemplate = cha.getTemplate().getIdTemplate();
        _isAttackable = cha.isAutoAttackable(attacker);
        _rhand = cha.getRightHandItem();
        _lhand = cha.getLeftHandItem();
        _isSummoned = false;
        _collisionHeight = cha.getCollisionHeight();
        _collisionRadius = cha.getCollisionRadius();
        if (cha.getTemplate().isServerSideName())
            _name = cha.getName();

        if (cha.isChampion() )
        {
            _title = (Config.CHAMPION_TITLE); 
        }
        else if (cha.getTemplate().isServerSideTitle())
        {
            _title = cha.getTemplate().getTitle();
        }
        else
        {
            _title = cha.getTitle();
        }
        
        if (Config.SHOW_NPC_LVL && _activeChar instanceof L2MonsterInstance)
        {
            String t = "Lv " + cha.getLevel() + (cha.getAggroRange() > 0 ? "*" : "");
            if (_title != null && !_title.equals(""))
                t += " " + _title;
            
            _title = t;
        }
        
        _x = _activeChar.getX();
        _y = _activeChar.getY();
        _z = _activeChar.getZ();
        _heading = _activeChar.getHeading();
        _mAtkSpd = _activeChar.getMAtkSpd();
        _pAtkSpd = _activeChar.getPAtkSpd();
        _runSpd = _activeChar.getRunSpeed();
        _walkSpd = _activeChar.getStat().getWalkSpeed();
        _swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
        _swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
    }
    
    public NpcInfo(L2Summon cha, L2Character attacker)
    {
        _activeChar = cha;
        _idTemplate = cha.getTemplate().getIdTemplate();
        _isAttackable = cha.isAutoAttackable(attacker); //(cha.getKarma() > 0);
        _rhand = 0;
        _lhand = 0;
        _isSummoned = cha.isShowSummonAnimation();
        _collisionHeight = _activeChar.getTemplate().getCollisionHeight();
        _collisionRadius = _activeChar.getTemplate().getCollisionRadius();
        if (cha.getTemplate().isServerSideName() || cha instanceof L2PetInstance)
        {
            _name = _activeChar.getName();
            _title = _activeChar.getTitle();
        }
        
        _x = _activeChar.getX();
        _y = _activeChar.getY();
        _z = _activeChar.getZ();
        _heading = _activeChar.getHeading();
        _mAtkSpd = _activeChar.getMAtkSpd();
        _pAtkSpd = _activeChar.getPAtkSpd();
        _runSpd = _activeChar.getRunSpeed();
        _walkSpd = _activeChar.getStat().getWalkSpeed();
        _swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
        _swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
    }

    public NpcInfo(L2Trap cha, L2Character attacker)
    {
        _activeChar = cha;
        _idTemplate = cha.getTemplate().getIdTemplate();
        _isAttackable = cha.isAutoAttackable(attacker);
        _rhand = 0;
        _lhand = 0;
        _collisionHeight = _activeChar.getTemplate().getCollisionHeight();
        _collisionRadius = _activeChar.getTemplate().getCollisionRadius();
        _x = _activeChar.getX();
        _y = _activeChar.getY();
        _z = _activeChar.getZ();
        _title = cha.getOwner().getName();
        _heading = _activeChar.getHeading();
        _mAtkSpd = _activeChar.getMAtkSpd();
        _pAtkSpd = _activeChar.getPAtkSpd();
        _runSpd = _activeChar.getRunSpeed();
        _walkSpd = _activeChar.getStat().getWalkSpeed();
        _swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
        _swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
    }

    public NpcInfo(L2Decoy cha)
    {
        _idTemplate = cha.getTemplate().getIdTemplate();
        _activeChar = cha;
        _x = _activeChar.getX();
        _y = _activeChar.getY();
        _z = _activeChar.getZ();
        _heading = cha.getOwner().getHeading();
        _mAtkSpd = cha.getMAtkSpd();
        _pAtkSpd = cha.getOwner().getPAtkSpd();
        _runSpd = cha.getOwner().getRunSpeed();
        _walkSpd = cha.getOwner().getStat().getWalkSpeed();
        _swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
        _swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
    }

    @Override
    protected final void writeImpl()
    {
        if (_idTemplate > 13070 && _idTemplate < 13077)
        {
            L2PcInstance owner = ((L2Decoy)_activeChar).getOwner();
            writeC(0x31);
            writeD(_x);
            writeD(_y);
            writeD(_z);
            writeD(_heading);
            writeD(_activeChar.getObjectId());
            writeS(owner.getAppearance().getVisibleName());
            writeD(owner.getRace().ordinal());
            writeD(owner.getAppearance().getSex()? 1 : 0);

            if (owner.getClassIndex() == 0)
                writeD(owner.getClassId().getId());
            else
                writeD(owner.getBaseClass());

            Inventory inv = owner.getInventory();
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_LRHAND));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
            writeD(inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR2));
            
             // T1 new d's 
            writeD(0x00); 
            writeD(0x00); 
            writeD(0x00); 
            writeD(0x00); 
            writeD(0x00); 
            writeD(0x00); 
            writeD(0x00); 
            writeD(0x00); 
             // end of t1 new d's 

            // c6 new h's
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeD(inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeD(inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_LRHAND));
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);
            writeH(0x00);

            
            // T1 new h's 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 
            writeH(0x00); 

            // end of t1 new h's 
            
            
            writeD(owner.getPvpFlag());
            writeD(owner.getKarma());

            writeD(_mAtkSpd);
            writeD(_pAtkSpd);

            writeD(owner.getPvpFlag());
            writeD(owner.getKarma());

            writeD(_runSpd);
            writeD(_walkSpd);
            writeD(50);  // swimspeed
            writeD(50);  // swimspeed
            writeD(_flRunSpd);
            writeD(_flWalkSpd);
            writeD(_flyRunSpd);
            writeD(_flyWalkSpd);
            writeF(owner.getStat().getMovementSpeedMultiplier()); // _activeChar.getProperMultiplier()
            writeF(owner.getStat().getAttackSpeedMultiplier()); // _activeChar.getAttackSpeedMultiplier()
            L2Summon pet = _activeChar.getPet();
            L2Transformation trans;
            if (owner.getMountType() != 0 && pet != null)
            {
                writeF(pet.getTemplate().getCollisionRadius());
                writeF(pet.getTemplate().getCollisionHeight());
            }
            else if ((trans = owner.getTransformation()) != null)
            {
                writeF(trans.getCollisionRadius());
                writeF(trans.getCollisionHeight());
            }
            else
            {
                writeF(owner.getBaseTemplate().getCollisionRadius());
                writeF(owner.getBaseTemplate().getCollisionHeight());
            }

            writeD(owner.getAppearance().getHairStyle());
            writeD(owner.getAppearance().getHairColor());
            writeD(owner.getAppearance().getFace());

            writeS(owner.getAppearance().getVisibleTitle());

            writeD(owner.getClanId());
            writeD(owner.getClanCrestId());
            writeD(owner.getAllyId());
            writeD(owner.getAllyCrestId());
            // In UserInfo leader rights and siege flags, but here found nothing??
            // Therefore RelationChanged packet with that info is required
            writeD(0);

            writeC(owner.isSitting() ? 0 : 1);    // standing = 1  sitting = 0
            writeC(owner.isRunning() ? 1 : 0);    // running = 1   walking = 0
            writeC(owner.isInCombat() ? 1 : 0);
            writeC(owner.isAlikeDead() ? 1 : 0);

            writeC(owner.getAppearance().getInvisible() ? 1 : 0); // invisible = 1  visible =0

            writeC(owner.getMountType()); // 1 on strider   2 on wyvern  3 on Great Wolf  0 no mount
            writeC(owner.getPrivateStoreType());   //  1 - sellshop

            writeH(owner.getCubics().size());
            for (int id : owner.getCubics().keySet())
                writeH(id);

            writeC(0x00);   // find party members

            writeD(owner.getAbnormalEffect());

            writeC(owner.getCharRecommendationStatus().getRecomLeft());                       //Changed by Thorgrim
            writeH(owner.getCharRecommendationStatus().getRecomHave()); //Blue value for name (0 = white, 255 = pure blue)
            writeD(owner.getMountNpcId() + 1000000);

            writeD(owner.getClassId().getId());
            writeD(0x00); //?
            writeC(owner.isMounted() ? 0 : owner.getEnchantEffect());

            if(owner.getTeam()==1)
                writeC(0x01); //team circle around feet 1= Blue, 2 = red
            else if(owner.getTeam()==2)
                writeC(0x02); //team circle around feet 1= Blue, 2 = red
            else
                writeC(0x00); //team circle around feet 1= Blue, 2 = red

            writeD(owner.getClanCrestLargeId());
            writeC(owner.isNoble() ? 1 : 0); // Symbol on char menu ctrl+I
            writeC(owner.isHero() ? 1 : 0); // Hero Aura

            writeC(owner.isFishing() ? 1 : 0); //0x01: Fishing Mode (Cant be undone by setting back to 0)
            writeD(owner.getFishx());
            writeD(owner.getFishy());
            writeD(owner.getFishz());

            writeD(owner.getAppearance().getNameColor());

            writeD(_heading);

            writeD(owner.getPledgeClass());
            writeD(owner.getSubPledgeType());

            writeD(owner.getAppearance().getTitleColor());

            if (owner.isCursedWeaponEquipped())
                writeD(CursedWeaponsManager.getInstance().getLevel(owner.getCursedWeaponEquippedId()));
            else
                writeD(0x00);

            // T1 
            if (owner.getClan() != null)
                writeD(owner.getClan().getReputationScore());
            else
                writeD(0x00); 

            // T1
            writeD(0x00); // Can Decoys be transformed?
            writeD(0x00); // Can Decoys have Agathions?
        }
        else
        {
            if (_activeChar instanceof L2Summon)
                if (((L2Summon)_activeChar).getOwner() != null 
                    && ((L2Summon)_activeChar).getOwner().getAppearance().getInvisible())
                    return;
            writeC(0x0c);
            writeD(_activeChar.getObjectId());
            writeD(_idTemplate+1000000);  // npctype id
            writeD(_isAttackable ? 1 : 0); 
            writeD(_x);
            writeD(_y);
            writeD(_z);
            writeD(_heading);
            writeD(0x00);
            writeD(_mAtkSpd);
            writeD(_pAtkSpd);
            writeD(_runSpd);
            writeD(_walkSpd);
            writeD(_swimRunSpd/*0x32*/);  // swimspeed
            writeD(_swimWalkSpd/*0x32*/);  // swimspeed
            writeD(_flRunSpd);
            writeD(_flWalkSpd);
            writeD(_flyRunSpd);
            writeD(_flyWalkSpd);
            writeF(1.1/*_activeChar.getProperMultiplier()*/);
            //writeF(1/*_activeChar.getAttackSpeedMultiplier()*/);
            writeF(_pAtkSpd/277.478340719);
            writeF(_collisionRadius);
            writeF(_collisionHeight);
            writeD(_rhand); // right hand weapon
            writeD(0);
            writeD(_lhand); // left hand weapon
            writeC(1);    // name above char 1=true ... ??
            writeC(_activeChar.isRunning() ? 1 : 0);
            writeC(_activeChar.isInCombat() ? 1 : 0);
            writeC(_activeChar.isAlikeDead() ? 1 : 0);
            writeC(_isSummoned ? 2 : 0); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
            writeS(_name);
            writeS(_title);
            writeD(0); // Title color 0=client default
            writeD(0);
            writeD(0000);  // hmm karma ??

            writeD(_activeChar.getAbnormalEffect());  // C2
            writeD(0000);  // C2
            writeD(0000);  // C2
            writeD(0000);  // C2
            writeD(0000);  // C2
            writeC(0000);  // C2

            writeC(0x00);  // C3  team circle 1-blue, 2-red 
            writeF(_collisionRadius);
            writeF(_collisionHeight);
            writeD(0x00);  // C4 
            writeD(0x00);  // C6 
            writeD(0x00);
            writeD(0x00);
        }
    }
    
    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.serverpackets.ServerBasePacket#getType()
     */
    @Override
    public String getType()
    {
        return _S__22_NPCINFO;
    }
}
