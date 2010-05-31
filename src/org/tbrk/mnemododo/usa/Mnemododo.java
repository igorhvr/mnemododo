/*
 * Copyright (C) 2010 Timothy Bourke
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.tbrk.mnemododo.usa;

import java.util.Locale;

public class Mnemododo
    extends org.tbrk.mnemododo.MnemododoMain
{
    protected void configureDemo()
    {
        Locale locale = this.getResources().getConfiguration().locale;

        is_demo = true;
        demo_imgson_path_override = "/android_asset/";
        package_name = "org.tbrk.mnemododo.usa";

        if (locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            demo_path = "/android_asset/USA-states-zh-rCN/";
        
        } else if (locale.equals(Locale.TRADITIONAL_CHINESE)) {
            demo_path = "/android_asset/USA-states-zh-rTW/";

        } else {
            demo_path = "/android_asset/USA-states/";
        }
    }
}
