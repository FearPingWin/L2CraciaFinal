# Made by disKret
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "246_PossessorOfAPreciousSoul_3"

#NPC
LADD = 30721
CARADINE = 31740
OSSIAN = 31741

#QUEST ITEM
CARADINE_LETTER = 7678
CARADINE_LETTER_LAST = 7679
WATERBINDER = 7591
EVERGREEN = 7592
RAIN_SONG = 7593
RELIC_BOX = 7594

#MOBS
PILGRIM_OF_SPLENDOR = 21541
JUDGE_OF_SPLENDOR = 21544
BARAKIEL = 25325

#CHANCE FOR DROP
CHANCE_FOR_DROP = 5

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "31740-4.htm" :
     if cond == 0 :
       st.setState(STARTED)
       st.takeItems(CARADINE_LETTER,1)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
   if event == "31741-2.htm" :
     if cond == 1 :
       st.set("cond","2")
       st.set("awaitsWaterbinder","1")
       st.set("awaitsEvergreen","1")
       st.playSound("ItemSound.quest_middle")
   if event == "31741-5.htm" :
     if cond == 3 :
       st.set("cond","4")
       st.takeItems(WATERBINDER,1)
       st.takeItems(EVERGREEN,1)
       st.playSound("ItemSound.quest_middle")
   if event == "31741-9.htm" :
     if cond == 5 :
       st.set("cond","6")
       st.takeItems(RAIN_SONG,1)
       st.giveItems(RELIC_BOX,1)
       st.playSound("ItemSound.quest_middle")
   if event == "30721-2.htm" :
     if cond == 6 :
       st.set("cond","0")
       st.takeItems(RELIC_BOX,1)
       st.giveItems(CARADINE_LETTER_LAST,1)
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><head><body>I have nothing to say you</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId != CARADINE and id != STARTED : return htmltext

   cond=st.getInt("cond")
   if st.getPlayer().isSubClassActive() :
     cond = st.getInt("cond")
     if npcId == CARADINE and cond == 0 and st.getQuestItemsCount(CARADINE_LETTER) == 1 :
       if id == COMPLETED :
         htmltext = "<html><head><body>This quest have already been completed.</body></html>"
       elif st.getPlayer().getLevel() < 65 : 
         htmltext = "31740-2.htm"
         st.exitQuest(1)
       elif st.getPlayer().getLevel() >= 65 :
         htmltext = "31740-1.htm"
     if npcId == CARADINE and cond == 1 :
       htmltext = "31740-5.htm"
     if npcId == OSSIAN and cond == 1 :
       htmltext = "31741-1.htm"
     if npcId == OSSIAN and cond == 2 :
       htmltext = "31741-4.htm"
     if npcId == OSSIAN and cond == 3 and st.getQuestItemsCount(WATERBINDER) == 1 and st.getQuestItemsCount(EVERGREEN) == 1 :
       htmltext = "31741-3.htm"
     if npcId == OSSIAN and cond == 4 :
       htmltext = "31741-8.htm"
     if npcId == OSSIAN and cond == 5 and st.getQuestItemsCount(RAIN_SONG) == 1 :
       htmltext = "31741-7.htm"
     if npcId == OSSIAN and cond == 6 and st.getQuestItemsCount(RELIC_BOX) == 1 :
       htmltext = "31741-11.htm"
     if npcId == LADD and cond == 6 :
       htmltext = "30721-1.htm"
   return htmltext

 def onKill (self,npc,player):
   npcId = npc.getNpcId()
   if npcId == PILGRIM_OF_SPLENDOR :
     #get a random party member who is doing this quest and needs this drop 
     partyMember = self.getRandomPartyMember(player,"awaitsWaterbinder","1")
     if partyMember :
         st = partyMember.getQuestState(qn)
         chance = st.getRandom(100)
         cond = st.getInt("cond")
         if st.getQuestItemsCount(WATERBINDER) < 1 :
           if chance < CHANCE_FOR_DROP :
             st.giveItems(WATERBINDER,1)
             st.unset("awaitsWaterbinder")
             if st.getQuestItemsCount(EVERGREEN) < 1 :
               st.playSound("ItemSound.quest_itemget")
             else:
               st.playSound("ItemSound.quest_middle")
               st.set("cond","3")
   if npcId == JUDGE_OF_SPLENDOR :
     #get a random party member who is doing this quest and needs this drop 
     partyMember = self.getRandomPartyMember(player,"awaitsEvergreen","1")
     if partyMember :
         st = partyMember.getQuestState(qn)
         chance = st.getRandom(100)
         cond = st.getInt("cond")
         if cond == 2 and st.getQuestItemsCount(EVERGREEN) < 1 :
           if chance < CHANCE_FOR_DROP :
             st.giveItems(EVERGREEN,1)
             st.unset("awaitsEvergreen")
             if st.getQuestItemsCount(WATERBINDER) < 1 :
               st.playSound("ItemSound.quest_itemget")
             else:
               st.playSound("ItemSound.quest_middle")
               st.set("cond","3")
   if npcId == BARAKIEL :
     #get a random party member who is doing this quest and needs this drop (cond == 4)
     partyMember = self.getRandomPartyMember(player,"4")
     if partyMember :
         st = partyMember.getQuestState(qn)
         cond = st.getInt("cond")
         if cond == 4 and st.getQuestItemsCount(RAIN_SONG) < 1 :
           st.giveItems(RAIN_SONG,1)
           st.playSound("ItemSound.quest_middle")
           st.set("cond","5")
   return 

QUEST       = Quest(246,qn,"Possessor Of A Precious Soul - 3")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(CARADINE)
QUEST.addTalkId(CARADINE)

QUEST.addTalkId(OSSIAN)
QUEST.addTalkId(LADD)

QUEST.addKillId(PILGRIM_OF_SPLENDOR)
QUEST.addKillId(JUDGE_OF_SPLENDOR)
QUEST.addKillId(BARAKIEL)

STARTED.addQuestDrop(CARADINE,CARADINE_LETTER_LAST,1)
STARTED.addQuestDrop(CARADINE,WATERBINDER,1)
STARTED.addQuestDrop(CARADINE,EVERGREEN,1)
STARTED.addQuestDrop(CARADINE,RAIN_SONG,1)
STARTED.addQuestDrop(CARADINE,RELIC_BOX,1)

print "importing quests: 246: Possessor Of A Precious Soul - 3"
