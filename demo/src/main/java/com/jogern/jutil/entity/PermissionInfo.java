package com.jogern.jutil.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2019-10-15.
 * @author zujianliang
 */
public class PermissionInfo {

      private String       mTitle;
      private int          mDrawableId;
      private List<String> mPermission;

      private PermissionInfo(String title, int drawableId, List<String> permission) {
            mTitle = title;
            mDrawableId = drawableId;
            mPermission = permission;
      }

      public String getTitle() {
            return mTitle;
      }

      public int getDrawableId() {
            return mDrawableId;
      }

      public void removePermission(String permission) {
            if (mPermission != null && permission != null) {
                  mPermission.remove(permission);
            }
      }

      public int getPermissionSize() {
            return mPermission == null ? 0 : mPermission.size();
      }

      public String[] getPermission() {
            if (mPermission != null) {
                  return mPermission.toArray(new String[0]);
            }
            return null;
      }

      public static class Builder {

            private String       mTitle;
            private int          mDrawableId;
            private List<String> mPermission;

            public Builder() {
                  mPermission = new ArrayList<>();
            }

            public Builder setTitle(String title) {
                  mTitle = title;
                  return this;
            }

            public Builder setDrawableId(int drawableId) {
                  mDrawableId = drawableId;
                  return this;
            }

            public Builder addPermission(String permission) {
                  if (permission != null) {
                        mPermission.add(permission);
                  }
                  return this;
            }

            public Builder addPermission(String... permission) {
                  if (permission != null) {
                        for (String p : permission) {
                              addPermission(p);
                        }
                  }
                  return this;
            }

            public PermissionInfo build() {
                  return new PermissionInfo(mTitle, mDrawableId, mPermission);
            }
      }


}
