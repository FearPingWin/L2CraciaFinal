# Made by Mr. Have fun! - Version 0.3 by DrLecter
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

GLUDIO_LORDS_MARK = 1084
ORC_AMULET = 752
ORC_NECKLACE = 1085
WEREWOLF_FANG = 1086
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30039-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
      st.giveItems(GLUDIO_LORDS_MARK,1)
    elif event == "30039-05.htm" :
      st.takeItems(GLUDIO_LORDS_MARK,1)
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (Self,npc,st):
   htmltext = "<html><head><body>I have nothing to say you</body></html>"
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if int(st.get("cond"))==0 :
     if st.getPlayer().getLevel() >= 6 :
       htmltext = "30039-02.htm"
     else:
       htmltext = "30039-01.htm"
       st.exitQuest(1)
   else :
     orc_a=st.getQuestItemsCount(ORC_AMULET)
     orc_n=st.getQuestItemsCount(ORC_NECKLACE)
     wer_f=st.getQuestItemsCount(WEREWOLF_FANG)
     if orc_a==orc_n==wer_f==0 :
       htmltext = "30039-04.htm"
     else :
       st.giveItems(ADENA,5*orc_a+15*orc_n+10*wer_f)
       st.takeItems(ORC_AMULET,-1)
       st.takeItems(ORC_NECKLACE,-1)
       st.takeItems(WEREWOLF_FANG,-1)
       htmltext = "30039-07.htm"
   return htmltext

 def onKill (self,npc,st):
   npcId = npc.getNpcId()
   chance=5
   if npcId in [20130,20131,20006] :
     item = ORC_AMULET
   elif npcId in [20093,20096,20098] :
     item = ORC_NECKLACE
   else :
     item = WEREWOLF_FANG
     if npcId == 20343 : chance = 4
     elif npcId == 20342 : chance = 2
   if st.getQuestItemsCount(GLUDIO_LORDS_MARK) :
     if st.getRandom(10)<chance :
       st.giveItems(item,1)
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(257,"257_GuardIsBusy1","Guard Is Busy1")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30039)

CREATED.addTalkId(30039)
STARTING.addTalkId(30039)
STARTED.addTalkId(30039)
COMPLETED.addTalkId(30039)

STARTED.addKillId(20130)
STARTED.addKillId(20131)
STARTED.addKillId(20132)
STARTED.addKillId(20342)
STARTED.addKillId(20343)
STARTED.addKillId(20006)
STARTED.addKillId(20093)
STARTED.addKillId(20096)
STARTED.addKillId(20098)

STARTED.addQuestDrop(20130,ORC_AMULET,1)
STARTED.addQuestDrop(20093,ORC_NECKLACE,1)
STARTED.addQuestDrop(20132,WEREWOLF_FANG,1)
STARTED.addQuestDrop(30039,GLUDIO_LORDS_MARK,1)

print "importing quests: 257: Guard Is Busy1"
