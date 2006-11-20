# Made by Mr. - Version 0.3 by DrLecter
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

ORC_AMULET = 1114
ORC_NECKLACE = 1115
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30221-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30221-06.htm" :
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (Self,npc,st):
   htmltext = "<html><head><body>I have nothing to say you</body></html>"
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if int(st.get("cond"))==0 :
     if st.getPlayer().getRace().ordinal() != 1 :
       htmltext = "30221-00.htm"
       st.exitQuest(1)
     elif st.getPlayer().getLevel()<6 :
       htmltext = "30221-01.htm"
       st.exitQuest(1)
     else :
       htmltext = "30221-02.htm"
   else :
     amulet = st.getQuestItemsCount(ORC_AMULET)
     necklace = st.getQuestItemsCount(ORC_NECKLACE)
     if amulet == necklace == 0 :
       htmltext = "30221-04.htm"
     else :
       htmltext = "30221-05.htm"
       st.giveItems(ADENA,amulet*5+necklace*15)
       st.takeItems(ORC_AMULET,-1)
       st.takeItems(ORC_NECKLACE,-1)
   return htmltext

 def onKill (self,npc,st):
   item=ORC_AMULET
   if npc.getNpcId() in range(20471,20474) :
     item = ORC_NECKLACE
   if st.getRandom(10)>4 :
     st.giveItems(item,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(260,"260_HuntForOrcs1","Hunt For Orcs1")
CREATED     = State('Start', QUEST)
STARTING    = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30221)

CREATED.addTalkId(30221)
STARTING.addTalkId(30221)
STARTED.addTalkId(30221)
COMPLETED.addTalkId(30221)

STARTED.addKillId(20468)
STARTED.addKillId(20469)
STARTED.addKillId(20470)
STARTED.addKillId(20471)
STARTED.addKillId(20472)
STARTED.addKillId(20473)

STARTED.addQuestDrop(20468,ORC_AMULET,1)
STARTED.addQuestDrop(20472,ORC_NECKLACE,1)

print "importing quests: 260: Hunt For Orcs1"
