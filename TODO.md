# FinQuest — Frontend/Backend API Contract Alignment (Phase 4)

## Goal
Bring the frontend API layer up to the current JWT-based backend contract without modifying the backend, database, UI, or auth logic.

## Steps
- [x] Inspect current backend controllers & DTOs (profile, gamification, learning-path, quiz)
- [x] Inspect frontend API layer (api.js, endpoints.js, AuthContext.jsx, Dashboard.jsx, LearningPath.jsx, Levels.jsx, Quiz.jsx)
- [x] Confirm axios interceptor attaches `Authorization: Bearer <accessToken>`
- [ ] Update `endpoints.js`: profile → `GET /api/profile` (JWT, no userId); keep gamification `?userId=` (backend still requires it)
- [ ] Update `api.js`: add 401 refresh interceptor using existing `/auth/refresh` (no new auth system)
- [ ] Update `Dashboard.jsx`: consume ProfileDto fields from `GET /api/profile`; preserve UI/3D mentor/gamification
- [ ] Verify LearningPath/Levels/Quiz already use correct JWT endpoints (no change unless mismatch found)
- [ ] Run `npm run build`
- [ ] Live verification through Vite proxy (profile, gamification summary, learning path)
