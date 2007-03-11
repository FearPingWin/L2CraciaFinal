# Made by Polo - Have fun! - Fixed by BiTi
# v0.3.1 by DrLecter
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

#Npc
ELIYAH = 31329
FLAURON = 32010

#Items
MARK = 8064

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if htmltext == "31329-04.htm" :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
    elif htmltext == "32010-02.htm" :
       st.playSound("ItemSound.quest_finish")
       st.giveItems(MARK,1)
       st.unset("cond")
       st.setState(COMPLETED)
    return htmltext

 def onTalk (Self,npc,st):
   npcId = npc.getNpcId()
   htmltext = "<html><head><body>I have nothing to say to you</body></html>"
   id = st.getState()
   cond = st.getInt("cond")
   if npcId == ELIYAH :
      if cond == 0 and id == CREATED:
         if st.getPlayer().getLevel()>72 :
            htmltext = "31329-02.htm"
         else:
            htmltext = "31329-01.htm"
            st.exitQuest(1)
      else :
         htmltext = "31329-05.htm"
   elif npcId == FLAURON :
      if cond == 1 :
         htmltext = "32010-01.htm"
         st.set("cond","2")
      else :
         htmltext = "32010-03.htm"
   return htmltext


QUEST       = Quest(636,"636_TruthBeyond","The Truth Beyond the Gate")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(ELIYAH)

CREATED.addTalkId(ELIYAH)
STARTED.addTalkId(ELIYAH)
STARTED.addTalkId(FLAURON)

print "importing quests: 636: The Truth Beyond the Gate"
