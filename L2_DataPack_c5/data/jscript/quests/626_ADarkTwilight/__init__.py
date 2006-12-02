# Made by disKret
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

#NPC
HIERARCH = 31517

#ITEMS
BLOOD_OF_SAINT = 7169

#REWARDS
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   count = st.getQuestItemsCount(BLOOD_OF_SAINT)
   if event == "31517-1.htm" :
     st.set("cond","1")
     st.setState(STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "31517-3.htm" :
     if count < 300 :
        htmltext = "31517-3a.htm"
   elif event == "31517-4.htm" :
     if count < 300 :
        htmltext = "31517-3a.htm"
     else :
        st.addExpAndSp(162773,12500)
        st.takeItems(BLOOD_OF_SAINT,-1)
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   elif event == "31517-5.htm" :
     if count < 300 :
        htmltext = "31517-3a.htm"
     else :
        st.giveItems(ADENA,100000)
        st.takeItems(BLOOD_OF_SAINT,-1)
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   return htmltext

 def onTalk (Self,npc,st):
   htmltext = "<html><head><body>I have nothing to say you</body></html>"
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if cond == 0 :
     if st.getPlayer().getLevel() >= 60 : # and st.getPlayer().getLevel() <= 71
       htmltext = "31517-0.htm"
     else:
       htmltext = "31517-0a.htm"
       st.exitQuest(1)
   elif st.getQuestItemsCount(BLOOD_OF_SAINT) == 300 :
     htmltext = "31517-2.htm"
   else :
     htmltext = "31517-2a.htm"
   return htmltext

 def onKill (self,npc,st):
   count = st.getQuestItemsCount(BLOOD_OF_SAINT)
   if st.getInt("cond") == 1 and count < 300 :
      st.giveItems(BLOOD_OF_SAINT,1)
      if count == 299 :
        st.playSound("ItemSound.quest_middle")
        st.set("cond","2")
      else:
        st.playSound("ItemSound.quest_itemget")	
   return

QUEST       = Quest(626,"626_ADarkTwilight","A Dark Twilight")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(31517)
CREATED.addTalkId(31517)
STARTED.addTalkId(31517)

for mobs in range(21520,21541):
  STARTED.addKillId(mobs)

STARTED.addQuestDrop(21520,BLOOD_OF_SAINT,1)

print "importing quests: 626: A Dark Twilight"
