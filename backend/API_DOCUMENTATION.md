# Grupper Backend API Documentation

## Overview
Base URL: `http://localhost:8080/api/v1`

All responses return JSON. Error responses follow the format:
```json
{
  "error": "Error message description"
}
```

## HTTP Status Codes
- `200 OK` - Successful GET, PUT, DELETE
- `201 Created` - Successful POST
- `400 Bad Request` - Validation error or invalid parameters
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Groups API

### List Groups
```
GET /groups?page=1&limit=20
```

**Response:**
```json
{
  "groups": [
    {
      "id": 1,
      "name": "Tech Enthusiasts",
      "description": "A group for technology lovers",
      "imageUrl": "https://example.com/tech.jpg",
      "memberCount": 0,
      "postCount": 5,
      "createdAt": "2026-01-29T21:47:30.178568Z",
      "updatedAt": "2026-01-29T21:47:30.178568Z"
    }
  ],
  "totalCount": 1,
  "page": 1,
  "limit": 20
}
```

### Get Group
```
GET /groups/{id}
```

**Response:** Single group object (200 OK) or error (404)

### Create Group
```
POST /groups
Content-Type: application/json

{
  "name": "Tech Enthusiasts",
  "description": "A group for technology lovers",
  "imageUrl": "https://example.com/tech.jpg"
}
```

**Validation:**
- `name`: Required, 1-100 characters
- `description`: Optional, max 500 characters
- `imageUrl`: Optional, max 500 characters

**Response:** Created group object (201)

### Update Group
```
PUT /groups/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "description": "Updated description",
  "imageUrl": "https://example.com/new.jpg"
}
```

All fields optional. Only provided fields are updated.

**Response:** Updated group object (200) or error (404)

### Delete Group
```
DELETE /groups/{id}
```

Cascades to delete all tags and posts in the group.

**Response:** `{"message": "Group deleted successfully"}` (200) or error (404)

---

## Tags API

### List Tags for Group
```
GET /groups/{groupId}/tags
```

**Response:**
```json
[
  {
    "id": 1,
    "groupId": 1,
    "name": "Question",
    "color": "#8B5CF6",
    "createdAt": "2026-01-29T21:47:30.305493Z"
  },
  {
    "id": 2,
    "groupId": 1,
    "name": "Discussion",
    "color": "#10B981",
    "createdAt": "2026-01-29T21:47:30.368304Z"
  }
]
```

### Create Tag
```
POST /groups/{groupId}/tags
Content-Type: application/json

{
  "name": "Question",
  "color": "#8B5CF6"
}
```

**Validation:**
- `name`: Required, 1-30 characters
- `color`: Required, hex color format `#RRGGBB`

**Response:** Created tag object (201)

### Update Tag
```
PUT /groups/{groupId}/tags/{id}
Content-Type: application/json

{
  "name": "Updated Question",
  "color": "#FF0000"
}
```

All fields optional.

**Response:** Updated tag object (200) or error (404)

### Delete Tag
```
DELETE /groups/{groupId}/tags/{id}
```

Posts with this tag will have tagId set to null.

**Response:** `{"message": "Tag deleted successfully"}` (200) or error (404)

---

## Posts API

### List Posts for Group
```
GET /groups/{groupId}/posts?tag=1&sort=newest&page=1&limit=20
```

**Query Parameters:**
- `tag` (optional): Filter by tag ID
- `sort` (optional): `newest` (default), `oldest`, `most_commented`
- `page` (optional): Page number (default: 1)
- `limit` (optional): Posts per page (default: 20)

**Response:**
```json
{
  "posts": [
    {
      "id": 1,
      "groupId": 1,
      "title": "What's the best programming language?",
      "content": "I'm curious what everyone thinks. Share your opinions!",
      "authorName": "John Doe",
      "imageUrl": null,
      "tag": {
        "id": 1,
        "groupId": 1,
        "name": "Question",
        "color": "#8B5CF6",
        "createdAt": "2026-01-29T21:47:30.305493Z"
      },
      "commentCount": 3,
      "createdAt": "2026-01-29T21:47:30.458442Z",
      "updatedAt": "2026-01-29T21:47:30.458442Z"
    }
  ],
  "totalCount": 1,
  "page": 1,
  "limit": 20
}
```

### Get Post
```
GET /posts/{id}
```

**Response:** Single post object with tag details (200) or error (404)

### Create Post
```
POST /groups/{groupId}/posts
Content-Type: application/json

{
  "title": "What's the best programming language?",
  "content": "I'm curious what everyone thinks. Share your opinions!",
  "authorName": "John Doe",
  "tagId": 1,
  "imageUrl": null
}
```

**Validation:**
- `title`: Required, 1-100 characters
- `content`: Required, 1-5000 characters
- `authorName`: Required, 1-50 characters
- `tagId`: Required (must be valid tag ID)
- `imageUrl`: Optional, max 500 characters

**Response:** Created post object with tag details (201)

### Update Post
```
PUT /posts/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "content": "Updated content",
  "tagId": 2,
  "imageUrl": "https://example.com/image.jpg"
}
```

All fields optional.

**Response:** Updated post object (200) or error (404)

### Delete Post
```
DELETE /posts/{id}
```

Cascades to delete all comments on the post.

**Response:** `{"message": "Post deleted successfully"}` (200) or error (404)

---

## Comments API

### List Comments for Post
```
GET /posts/{postId}/comments
```

Returns nested/threaded comment structure. Top-level comments sorted newest first, replies sorted oldest first.

**Response:**
```json
[
  {
    "id": 1,
    "postId": 1,
    "parentId": null,
    "authorName": "Jane Smith",
    "content": "I think Python is great for beginners!",
    "createdAt": "2026-01-29T21:47:30.583893Z",
    "updatedAt": "2026-01-29T21:47:30.583893Z",
    "replies": [
      {
        "id": 2,
        "postId": 1,
        "parentId": 1,
        "authorName": "Bob Johnson",
        "content": "I agree! Python syntax is very readable.",
        "createdAt": "2026-01-29T21:47:30.655279Z",
        "updatedAt": "2026-01-29T21:47:30.655279Z",
        "replies": []
      }
    ]
  }
]
```

### Get Comment
```
GET /comments/{id}
```

**Response:** Single comment object (200) or error (404)

Note: This returns the comment without nested replies. Use GET /posts/{postId}/comments for nested structure.

### Create Comment
```
POST /posts/{postId}/comments
Content-Type: application/json

{
  "authorName": "Jane Smith",
  "content": "I think Python is great for beginners!"
}
```

**Validation:**
- `authorName`: Required, 1-50 characters
- `content`: Required, 1-2000 characters

**Response:** Created comment object (201)

### Create Reply
```
POST /comments/{id}/reply
Content-Type: application/json

{
  "authorName": "Bob Johnson",
  "content": "I agree! Python syntax is very readable."
}
```

**Validation:** Same as Create Comment

**Response:** Created reply object (201)

### Update Comment
```
PUT /comments/{id}
Content-Type: application/json

{
  "content": "Updated comment content"
}
```

**Validation:**
- `content`: Required, 1-2000 characters

**Response:** Updated comment object (200) or error (404)

### Delete Comment
```
DELETE /comments/{id}
```

Cascades to delete all replies to this comment.

**Response:** `{"message": "Comment deleted successfully"}` (200) or error (404)

---

## Data Models

### Group
```kotlin
{
  id: Long
  name: String
  description: String
  imageUrl: String?
  memberCount: Int
  postCount: Int
  createdAt: String (ISO 8601)
  updatedAt: String (ISO 8601)
}
```

### Tag
```kotlin
{
  id: Long
  groupId: Long
  name: String
  color: String (hex format #RRGGBB)
  createdAt: String (ISO 8601)
}
```

### Post
```kotlin
{
  id: Long
  groupId: Long
  title: String
  content: String
  authorName: String
  imageUrl: String?
  tag: Tag? (full tag object or null)
  commentCount: Int
  createdAt: String (ISO 8601)
  updatedAt: String (ISO 8601)
}
```

### Comment
```kotlin
{
  id: Long
  postId: Long
  parentId: Long? (null for top-level comments)
  authorName: String
  content: String
  createdAt: String (ISO 8601)
  updatedAt: String (ISO 8601)
  replies: List<Comment> (nested comments, empty if no replies)
}
```

---

## Running the Backend

```bash
cd backend
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew run
```

Server runs on: `http://localhost:8080`

Health check: `http://localhost:8080/health`

API info: `http://localhost:8080/api/v1`

---

## Notes for Mobile Developers

1. **Nested Comments**: The GET /posts/{postId}/comments endpoint returns a fully nested structure. You don't need to manually build the tree - it's already done by the backend.

2. **Tag Details in Posts**: When you fetch posts, the tag object is fully populated (not just an ID). This saves you an extra API call.

3. **Counters**: postCount on groups and commentCount on posts are automatically maintained. You don't need to calculate them.

4. **Sorting**: Posts support three sort modes:
   - `newest` (default): Most recent posts first
   - `oldest`: Oldest posts first
   - `most_commented`: Posts with most comments first

5. **Filtering**: Filter posts by tag using `?tag={tagId}` query parameter.

6. **Pagination**: All list endpoints support pagination. Use `page` and `limit` parameters.

7. **Cascading Deletes**: 
   - Deleting a group deletes all its tags and posts
   - Deleting a post deletes all its comments
   - Deleting a comment deletes all its replies
   - Deleting a tag sets post.tagId to null (doesn't delete posts)

8. **Validation**: All create/update endpoints validate input. Check the validation rules above for each field.

