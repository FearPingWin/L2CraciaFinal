#
# Created by DraX on 2005.08.12
# minor fixes by DrLecter 2005.09.10

print "importing village master data: Clan                   ...done"

import sys

from net.sf.l2j.gameserver.model.quest        import State
from net.sf.l2j.gameserver.model.quest        import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest


NPC=[30026,30031,30037,30066,30070,30109,30115,30120,30154,30174,30175,30176,30187,30191,30195,30288,30289,30290,30297,30358,30373,30462,30474,30498,30499,30500,30503,30504,30505,30508,30511,30512,30513,30520,30525,30565,30594,30595,30676,30677,30681,30685,30687,30689,30694,30699,30704,30845,30847,30849,30854,30857,30862,30865,30894,30897,30900,30905,30910,30913,31269,31272,31276,31279,31285,31288,31314,31317,31321,31324,31326,31328,31331,31334,31755]

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st):
   htmltext     = event
   Level        = st.getPlayer().getLevel()
   ClanLeader   = st.player.isClanLeader()
   PlayerinClan = st.player.getClanId()

   if event == "9000-01.htm": htmltext = "9000-01.htm"
   # Player must be Level 10 or above! (so cannot create clan)
   elif event == "9000-02.htm" and Level <= 9: htmltext = "9000-06.htm"
   # player is always clanleader! (so cannot create clan)
   elif event == "9000-02.htm" and ClanLeader == 1: htmltext = "9000-07.htm"
   # player is already in a clan! (so cannot create clan)
   elif event == "9000-02.htm" and PlayerinClan != 0: htmltext = "9000-09.htm"
   # always shown the clan raise page!
   elif event == "9000-03.htm": htmltext = "9000-03.htm"
   # player must be clanleader to dissolve clan!
   elif event == "9000-04.htm" and ClanLeader == 1: htmltext = "9000-04.htm"
   # player must be clanleader to dissolve clan! 
   elif event == "9000-04.htm" and PlayerinClan != 0: htmltext = "9000-08.htm"
   # player must be in a clan to dissolve clan! 
   elif event == "9000-04.htm" and PlayerinClan == 0: htmltext = "9000-11.htm"
   elif event == "9000-05.htm": htmltext = "9000-05.htm"
   elif event == "9000-12.htm": htmtext = "9000-12.htm"
   elif event == "9000-13.htm": htmtext = "9000-13.htm"
   elif event == "9000-14.htm": htmtext = "9000-14.htm"
   else: htmltext = "9000-02.htm"
   #st.exitQuest(1)
   return htmltext

 def onTalk (Self,npc,st):

   npcId = npc.getNpcId()
   if npcId in NPC:
     st.set("cond","0")
     st.setState(STARTED)
     return "9000-01.htm"

QUEST       = Quest(9000,"9000_clan","village_master")
CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)


for item in NPC:
### Quest NPC starter initialization
   QUEST.addStartNpc(item)
### Quest NPC initialization
   STARTED.addTalkId(item)
