// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;

public final class FindMeetingQuery {
  /**
    * Returns whether or not an event is attended by the required attendees from the Meeting Request
    *
    @param Event event The input event to compare against
    @param MeetingRequest request The subject meeting request
    @return a boolean value that is true if the event and request have overlapping attendees.
  */
  private static boolean isAttended(Event event, MeetingRequest request) {
    Collection<String> eventAttendees = event.getAttendees();
    Collection<String> requestAttendees = request.getAttendees();
    for (String attendee : eventAttendees) if (requestAttendees.contains(attendee)) return true;

    return false; // Fallback for if no attendees are found that belong to both the event and the meeting request
  }

  /**
    * Returns the minutes in a day from a half hour slot.
    *
    @param int halfHour The halfhour slot to be converted.
    @exception IllegalArgumentException thrown if slot < 0 or slot > 47.
  */
  private static int getTime(int halfHour) {
    if(halfHour < 0 || halfHour > 47 ) throw new IllegalArgumentException("Time out of bounds.");
    
    return halfHour * 30;
  }

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    throw new UnsupportedOperationException("TODO: Implement this method.");
  }
}
