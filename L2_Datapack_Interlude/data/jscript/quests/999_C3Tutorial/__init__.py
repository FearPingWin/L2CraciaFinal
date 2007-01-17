# Made by Mr. Have fun! Version 0.2
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

RECOMMENDATION_01 = 1067
RECOMMENDATION_02 = 1068
LEAF_OF_MOTHERTREE = 1069
BLOOD_OF_JUNDIN = 1070
LICENSE_OF_MINER = 1498
VOUCHER_OF_FLAME = 1496
SOULSHOT_NOVICE = 5789
SPIRITSHOT_NOVICE = 5790
BLUE_GEM=6353

# event:[htmlfile,radarX,radarY,radarZ,item,classId1,gift1,count1,classId2,gift2,count2]
EVENTS={
"30008_02":["30008-03.htm",-84058,243239,-3730,RECOMMENDATION_01,0x00,SOULSHOT_NOVICE,200,0,0,0],
"30017_02":["30017-03.htm",-84058,243239,-3730,RECOMMENDATION_02,0x0a,SPIRITSHOT_NOVICE,100,0,0,0],
"30370_02":["30370-03.htm",45491,48359,-3086,LEAF_OF_MOTHERTREE,0x19,SPIRITSHOT_NOVICE,100,0x12,SOULSHOT_NOVICE,200],
"30129_02":["30129-03.htm",12116,16666,-4610,BLOOD_OF_JUNDIN,0x26,SPIRITSHOT_NOVICE,100,0x1f,SOULSHOT_NOVICE,200],
"30528_02":["30528-03.htm",115642,-178046,-941,LICENSE_OF_MINER,0x35,SOULSHOT_NOVICE,200,0,0,0],
"30573_02":["30573-03.htm",-45067,-113549,-235,VOUCHER_OF_FLAME,0x31,SPIRITSHOT_NOVICE,100,0x2c,SOULSHOT_NOVICE,200]
}

# npcId:[raceId,[htmlfiles],npcTyp,item]
TALKS={
30017:[0,["30017-01.htm","30017-02.htm","30017-04.htm"],0,0],
30008:[0,["30008-01.htm","30008-02.htm","30008-04.htm"],0,0],
30370:[1,["30370-01.htm","30370-02.htm","30370-04.htm"],0,0],
30129:[2,["30129-01.htm","30129-02.htm","30129-04.htm"],0,0],
30573:[3,["30573-01.htm","30573-02.htm","30573-04.htm"],0,0],
30528:[4,["30528-01.htm","30528-02.htm","30528-04.htm"],0,0],
30018:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30019:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30020:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30021:[0,["30131-01.htm",0,"30019-03a.htm","30019-04.htm",],1,RECOMMENDATION_02],
30009:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30011:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30012:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30056:[0,["30530-01.htm","30009-03.htm",0,"30009-04.htm",],1,RECOMMENDATION_01],
30400:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30401:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30402:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30403:[1,["30131-01.htm","30400-03.htm","30400-03a.htm","30400-04.htm",],1,LEAF_OF_MOTHERTREE],
30131:[2,["30131-01.htm","30131-03.htm","30131-03a.htm","30131-04.htm",],1,BLOOD_OF_JUNDIN],
30404:[2,["30131-01.htm","30131-03.htm","30131-03a.htm","30131-04.htm",],1,BLOOD_OF_JUNDIN],
30574:[3,["30575-01.htm","30575-03.htm","30575-03a.htm","30575-04.htm",],1,VOUCHER_OF_FLAME],
30575:[3,["30575-01.htm","30575-03.htm","30575-03a.htm","30575-04.htm",],1,VOUCHER_OF_FLAME],
30530:[4,["30530-01.htm","30530-03.htm",0,"30530-04.htm",],1,LICENSE_OF_MINER]
}    

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    htmlfile,radarX,radarY,radarZ,item,classId1,gift1,count1,classId2,gift2,count2 = EVENTS[event]
    st.addRadar(radarX,radarY,radarZ);
    htmltext=htmlfile
    if st.getQuestItemsCount(item) and int(st.get("onlyone")) == 0:
         if st.getPlayer().getClassId().getId() == classId1 :
          st.addExpAndSp(0,50)
          st.takeItems(item,1)
          st.giveItems(gift1,count1)
          st.set("cond","0")
          st.set("onlyone","1")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
         elif st.getPlayer().getClassId().getId() == classId2 :
          st.addExpAndSp(0,50)
          st.takeItems(item,1)
          if gift2:
           st.giveItems(gift2,count2)
          st.set("cond","0")
          st.set("onlyone","1")
          st.setState(COMPLETED)
          st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (Self,npc,st):
   npcId = npc.getNpcId()
   htmltext = "<html><head><body>I have no tasks for you right now.</body></html>"
   raceId,htmlfiles,npcTyp,item = TALKS[npcId]
   id = st.getState()
   if id == CREATED :
     st.setState(STARTING)
     st.set("cond","0")
     st.set("onlyone","0")
     st.set("id","0")
   if (st.getPlayer().getLevel() >= 10 or int(st.get("onlyone"))) and npcTyp == 1:
       htmltext = "30575-05.htm"
   elif int(st.get("onlyone")) == 0 and st.getPlayer().getLevel() < 10 :
    if st.getPlayer().getRace().ordinal() == raceId :
      htmltext=htmlfiles[0]
      if npcTyp==1:
       if int(st.get("cond"))==0 :
        if st.getPlayer().getClassId().isMage() :
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_tutorial")
        else:
         htmltext="30530-01.htm"
         st.set("cond","1")
         st.setState(STARTED)
         st.playSound("ItemSound.quest_tutorial")
       elif int(st.get("cond"))==1 and st.getQuestItemsCount(item)==0 :
         if st.getQuestItemsCount(BLUE_GEM) :
           st.takeItems(BLUE_GEM,st.getQuestItemsCount(BLUE_GEM))
           st.giveItems(item,1)
           st.set("cond","2")
           st.playSound("ItemSound.quest_middle")
           if st.getPlayer().getClassId().isMage() :
             st.giveItems(SPIRITSHOT_NOVICE,100)
             htmltext = htmlfiles[2]
           else:
             st.giveItems(SOULSHOT_NOVICE,200)
             htmltext = htmlfiles[1]
         else:
           if st.getPlayer().getClassId().isMage() :
             htmltext = "30131-02.htm"
             if st.getPlayer().getRace().ordinal() == 3 :
              htmltext = "30575-02.htm"
           else:
             htmltext = "30530-02.htm"
       elif int(st.get("cond"))==2 :
        htmltext = htmlfiles[3]
      elif npcTyp == 0 :
        if int(st.get("cond"))==1 :
          htmltext = htmlfiles[0]
        elif int(st.get("cond"))==2 :
          htmltext = htmlfiles[1]
        elif int(st.get("cond"))==3 :
          htmltext = htmlfiles[2] 
   else:
       htmltext = "<html><head><body>You are too experienced now.</body></html>"
   return htmltext

 def onKill (self,npc,st):
   if int(st.get("cond"))==1 and st.getRandom(100) < 25 and st.getQuestItemsCount(BLUE_GEM) == 0 :
      st.giveItems(BLUE_GEM,1)
      st.playSound("ItemSound.quest_itemget")
      st.playSound("ItemSound.quest_tutorial")
   return

QUEST       = Quest(999,"999_C3Tutorial","C3 Tutorial")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)


QUEST.setInitialState(CREATED)

for startNpc in [30008,30009,30017,30019,30129,30131,30404,30056,30011,30012,30401,30403,30402,30018,30021,30020,30574,30370,30400,30528,30530,30573,30575]:
  QUEST.addStartNpc(startNpc)
  STARTING.addTalkId(startNpc)
  STARTED.addTalkId(startNpc)


STARTED.addKillId(20001)
STARTED.addKillId(27198)

print "importing quests: 999: C3 Tutorial"
