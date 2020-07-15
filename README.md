# PersonalApp
Personal App built for FBU 2020

## Overview
### Description
Allows travelers to connect with "hometown guides", people who live in their destination. The hometown guides would be tour guides, showing the visitors an authentic view of the destination -- as opposed to the "tourist-y" attractions.

### App Evaluation
- **Category:** Matching
- **Mobile:** Mobile is a handy platform to build on, as it allows visitors to find a hometown guide while on the go. When traveling, people may not carry a laptop, so having an app would allow them to make plans while abroad.
- **Story:** Allows visitors to see the authentic side of their destination, while encouraging cultural enrichment among both parties (visitor and guide).
- **Market:** Travelers who want to explore the non-touristy side of their destination and hometown guides could give tours as a side-gig, similar to Uber or Lyft.
- **Habit:** Creates a more personal experience when traveling and allows travelers to see things they otherwise wouldn't. For hometown guides, the motivation would be in the compensation (they would charge for their time, which is similar to rideshare drivers charging for rides)
- **Scope:** V1 would allow travelers to see which guides are available during their vacation dates and what experiences they are offering, and connect with them (more like couch surfing/airbnb, which is planned in advance). V2 might involve implementing a real-time method where travelers could look for guides who are availabe right at that moment to take them on a tour (more like Uber or lyft, which is on demand). 

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can login
* Users can create a new account
* Users can make a listing for giving tours
    * (Should this be a listing of tour guides or example itineraries?)
* Users can look through listings in an area
* Users can view details about a listing
* Users can delete their listing
* Users can take and set a profile photo


**Optional Nice-to-have Stories**

* Travelers and guides can chat in-app
* Travelers can leave reviews for guides
* Guides can add photos to their listings
* Guides can archive/deactivate listings instead of deleting
* Search for specific users

### 2. Screen Archetypes

* Login Screen
   * Users can login
* Registration Screen
   * Users can create an account
* Creation
    * Users can make a listing for giving tours
* Stream
    * Users can look through listings in an area
* Detail
    * Users can view details about a listing
* Profile
    * Users can take and set a profile photo
    * Users can delete their listing (?)
        * show listings on profile?
* View Reviews
   * Users can view reviews of another user
* Edit Profile
   * Users can edit their profile
* Edit Listing
   * Users can edit their listing

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Listing stream
* Create listing
* Profile

**Flow Navigation** (Screen to Screen)

* Login screen
   * => Home/Stream (like instagram)
* Registration screen
   * => Edit Profile (fill in details of profile)
      * => Profile
* Stream
    * => Details of listing
* Creation 
    * => Stream
* Detail 
    * => Maybe the guide's profile
    * => Edit Listing (if listing is the user's)
* Profile
    * => Edit Profile
    * => View Reviews
    
## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/erinwu25/PersonalApp/blob/master/wireframe3.jpg" width=600>
<img src="https://github.com/erinwu25/PersonalApp/blob/master/wireframe2.jpg" width=600>
<img src="https://github.com/erinwu25/PersonalApp/blob/master/wireframe1.jpg" width=600>

## Schema 

### Models
#### User
| Property | Type | Description | 
| ----------- | ----------- | ---- |
| Name      | String  | User's Name |
| Location   | String  | User's home location |
| Bio      | String  | A little description of the user |
| email | String  | User's email |
| Rating | Number  | User's rating (starts at 0) |

#### Listing
| Property | Type | Description | 
| ----------- | ----------- | ---- |
| Poster      | Pointer to User object  | User who created the listing |
| Blurb      | String  | A little description from the user |
| createdAt | DateTime  | time of listing creation |
| Availability | DateTime  | might make this a range of dates |

#### Review
| Property | Type | Description | 
| ----------- | ----------- | ---- |
| Rating      | Number  | Numerical rating maybe out of 5 |
| fromUser   | Pointer to User object  | user who is creating the review |
| toUser      | Pointer to User Object  | user who the review is about |
| description | String  | description/review blurb |
| createdAt | DateTime  | time of review creation |





### Networking
#### List of network requests by screen
* Home/Listing Stream Screen
   * (Read/GET) Query all listings in an area
* Post Detail Screen
   * (Read/GET) Query selected user object
   * (Read/GET) Query all reviews about the selected user
* Listing Creation Screen
    * (Create/PUT) Create new listing
    * (Read/GET) Query logged in user object
* Profile Detail Screen
  * (Read/GET) Query logged in user object
  * (Read/GET) Query all reviews about the logged in user
  * (Read/GET) Query all posts from the logged in user
  * (Delete) Delete post
* Create Account screen
  * (Create/PUT) Create new user
* Login Screen
  * (Read/GET) Query user according to login details
  
