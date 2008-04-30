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
package net.sf.l2j.gameserver.communitybbs.dao.forum.impl;

import net.sf.l2j.gameserver.communitybbs.dao.forum.PostsDAO;
import net.sf.l2j.gameserver.communitybbs.model.forum.Posts;
import net.sf.l2j.tools.dao.impl.BaseRootDAOHib;

/**
 * Home object for domain model class Posts.
 * @see net.sf.l2j.gameserver.communitybbs.model.forum.Posts
 * @author Hibernate Tools
 */
public class PostsDAOHib extends BaseRootDAOHib implements PostsDAO
{

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.PostsDAO#modifyPost(net.sf.l2j.gameserver.communitybbs.model.forum.Posts)
	 */
	public void modifyPost(Posts obj)
	{
		saveOrUpdate(obj);
	}

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.PostsDAO#createPost(net.sf.l2j.gameserver.communitybbs.model.forum.Posts)
	 */
	public int createPost(Posts obj)
	{
		return (Integer)save(obj);
	}

	/**
	 * @see net.sf.l2j.gameserver.communitybbs.dao.forum.PostsDAO#getPostById(java.lang.Integer)
	 */
	public Posts getPostById(Integer id)
	{
		return (Posts)get(Posts.class, id);
	} 
}