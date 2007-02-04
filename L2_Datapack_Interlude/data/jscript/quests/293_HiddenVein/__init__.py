# Made by Mr. - Version 0.3 by DrLecter
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

CHRYSOLITE_ORE = 1488
TORN_MAP_FRAGMENT = 1489
HIDDEN_VEIN_MAP = 1490
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30535-03.htm" :
      st.set("cond","1")
      st.setState(STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "30535-06.htm" :
      st.takeItems(TORN_MAP_FRAGMENT,-1)
      st.exitQuest(1)
      st.playSound("ItemSound.quest_finish")
    elif event == "30539-02.htm" :
      if st.getQuestItemsCount(TORN_MAP_FRAGMENT) >=4 :
        htmltext = "30539-03.htm"
        st.giveItems(HIDDEN_VEIN_MAP,1)
        st.takeItems(TORN_MAP_FRAGMENT,4)
    return htmltext

 def onTalk (Self,npc,st):
   npcId = npc.getNpcId()
   htmltext = "<html><head><body>I have nothing to say you</body></html>"
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if npcId == 30535 :
     if int(st.get("cond"))==0 :
       if st.getPlayer().getRace().ordinal() != 4 :
         htmltext = "30535-00.htm"
         st.exitQuest(1)
       elif st.getPlayer().getLevel() >= 6 :
         htmltext = "30535-02.htm"
         return htmltext
       else:
         htmltext = "30535-01.htm"
         st.exitQuest(1)
     else :
       if st.getQuestItemsCount(CHRYSOLITE_ORE)==0 :
         if st.getQuestItemsCount(HIDDEN_VEIN_MAP)==0 :
           htmltext = "30535-04.htm"
         else :
           htmltext = "30535-08.htm"
           st.giveItems(ADENA,st.getQuestItemsCount(HIDDEN_VEIN_MAP)*1000)
           st.takeItems(HIDDEN_VEIN_MAP,-1)
       else :
         if st.getQuestItemsCount(HIDDEN_VEIN_MAP)==0 :
           htmltext = "30535-05.htm"
           st.giveItems(ADENA,st.getQuestItemsCount(CHRYSOLITE_ORE)*10)
           st.takeItems(CHRYSOLITE_ORE,-1)
         else :
           htmltext = "30535-09.htm"
           st.giveItems(ADENA,st.getQuestItemsCount(CHRYSOLITE_ORE)*10+st.getQuestItemsCount(HIDDEN_VEIN_MAP)*1000)
           st.takeItems(HIDDEN_VEIN_MAP,-1)
           st.takeItems(CHRYSOLITE_ORE,-1)
   elif npcId == 30539 :
      htmltext = "30539-01.htm"
   return htmltext

 def onKill (self,npc,st):
   n = st.getRandom(100)
   if n > 50 :
     st.giveItems(CHRYSOLITE_ORE,1)
     st.playSound("ItemSound.quest_itemget")
   elif n < 5 :
     st.giveItems(TORN_MAP_FRAGMENT,1)
     st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(293,"293_HiddenVein","Hidden Vein")
CREATED     = State('Start', QUEST)
STARTING     = State('Starting', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(30535)

CREATED.addTalkId(30535)
STARTING.addTalkId(30535)
STARTED.addTalkId(30535)
COMPLETED.addTalkId(30535)

STARTED.addTalkId(30539)

STARTED.addKillId(20446)
STARTED.addKillId(20447)
STARTED.addKillId(20448)

STARTED.addQuestDrop(30539,HIDDEN_VEIN_MAP,1)
STARTED.addQuestDrop(20446,CHRYSOLITE_ORE,1)
STARTED.addQuestDrop(20447,TORN_MAP_FRAGMENT,1)

print "importing quests: 293: Hidden Vein"
