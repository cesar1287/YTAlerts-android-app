/*
 * SkyTube
 * Copyright (C) 2015  Ramon Mifsud
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation (version 3 of the License).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package free.rm.skytube.gui.businessobjects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An extended class of {@link RecyclerView.Adapter} that accepts a context and a list of items.
 */
public abstract class RecyclerViewAdapterEx<T, HolderType extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<HolderType> {

	private Context context;
	private LayoutInflater inflater;
	protected List<T> list;

	public RecyclerViewAdapterEx(Context context) {
		this(context, new ArrayList<T>());
	}

	public RecyclerViewAdapterEx(Context context, List<T> list) {
		this.context  = context;
		this.inflater = LayoutInflater.from(context);
		this.list     = list;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return An instance of {@link LayoutInflater}.
	 */
	protected LayoutInflater getLayoutInflater() {
		return inflater;
	}


	/**
	 * Clears the list and copy list l to the Adapter's list.
	 *
	 * @param l List to set
	 */
	public void setList(List<T> l) {
		clearList();
		appendList(l);
	}


	/**
	 * Append the given items to the Adapter's list.
	 *
	 * @param l The items to append.
	 */
	public void appendList(List<T> l) {
		if (l != null  &&  l.size() > 0) {
			this.list.addAll(l);
			this.notifyDataSetChanged();
		}
	}

	public void updateList(List<T> l) {
		list = l;
		notifyDataSetChanged();
	}


	/**
	 * Append the given item to the Adapter's list.
	 *
	 * @param item The item to append.
	 */
	protected void append(T item) {
		if (item != null) {
			this.list.add(item);
			this.notifyDataSetChanged();
		}
	}


	/**
	 * Remove an item from the Adapter's list.
	 *
	 * @param itemPosition	Item's position/index to remove.
	 */
	protected void remove(int itemPosition) {
		if (itemPosition >= 0  &&  itemPosition < getItemCount()) {
			list.remove(itemPosition);
		}
	}


	/**
	 * Clear all items that are in the list.
	 */
	public void clearList() {
		int listSize = getItemCount();

		this.list.clear();
		notifyItemRangeRemoved(0, listSize);
	}


	public Iterator<T> getIterator() {
		return this.list.iterator();
	}


	@Override
	public int getItemCount() {
		return list.size();
	}

	protected T get(int position) {
		return list.get(position);
	}

}
