package net.sf.l2j.gameserver.communitybbs.dao.forum.impl;

import java.util.List;
import java.util.Set;

import net.sf.l2j.gameserver.communitybbs.dao.forum.TopicDAO;
import net.sf.l2j.gameserver.communitybbs.model.forum.Posts;
import net.sf.l2j.gameserver.communitybbs.model.forum.Topic;
import net.sf.l2j.tools.dao.impl.BaseRootDAOHib;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;

// Generated 19 f�vr. 2007 22:07:55 by Hibernate Tools 3.2.0.beta8


/**
 * Home object for domain model class Topic.
 * @see net.sf.l2j.gameserver.communitybbs.model.forum.Topic
 */
public class TopicDAOHib extends BaseRootDAOHib implements TopicDAO
{

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.TopicDAO#createTopic(net.sf.l2j.gameserver.communitybbs.model.forum.Topic)
	 */
	public int createTopic(Topic obj)
	{
        return (Integer)save(obj);        
	}

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.TopicDAO#deleteTopic(net.sf.l2j.gameserver.communitybbs.model.forum.Topic)
	 */
	public void deleteTopic(Topic obj)
	{
		delete(obj);		
	}

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.TopicDAO#getTopicById(java.lang.Integer)
	 */
	public Topic getTopicById(Integer id)
	{
		return (Topic)get(Topic.class, id);
	}

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.TopicDAO#getTopicByName(java.lang.String)
	 */
	public Topic getTopicByName(String name)
	{
        return (Topic)getCurrentSession().createCriteria(Topic.class).add(Restrictions.eq("topicName", name)).uniqueResult();
	}

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.TopicDAO#getPaginatedTopicById(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getPaginatedTopicByForumId(Integer iPageSize, Integer iIdx, Integer id)
	{
		return (List <Topic>)getCurrentSession().createQuery(
				"from " + Topic.class.getName()+ " where topicForumId = "+id).setMaxResults(iPageSize).setFirstResult(iPageSize*iIdx).list();
	}

    /**
     * Initialize the object (force retrieval of associated objects
     * 
     * @param obj
     * @return a set of post
     */
    public Set<Posts> getPostses(Topic obj)
    {
        if ( ! Hibernate.isInitialized(obj.getPostses()))
        {
            getCurrentSession().refresh(obj);
        }
        return obj.getPostses();
    }
}
