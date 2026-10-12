import request from '../utils/request'

export const login = data => request.post('/login', data)
export const register = data => request.post('/register', data)
export const getUserInfo = () => request.get('/user/info')

export const listDepts = () => request.get('/depts')
export const addDept = data => request.post('/admin/depts', data)
export const updateDept = (id, data) => request.put(`/admin/depts/${id}`, data)
export const deleteDept = id => request.delete(`/admin/depts/${id}`)

export const pageEmps = params => request.get('/admin/emps', { params })
export const getEmp = id => request.get(`/admin/emps/${id}`)
export const addEmp = data => request.post('/admin/emps', data)
export const updateEmp = (id, data) => request.put(`/admin/emps/${id}`, data)
export const deleteEmps = ids => request.delete('/admin/emps', { params: { ids: ids.join(',') } })

export const pageNotices = params => request.get('/notices', { params })
export const getNotice = id => request.get(`/notices/${id}`)
export const addNotice = data => request.post('/admin/notices', data)
export const updateNotice = (id, data) => request.put(`/admin/notices/${id}`, data)
export const deleteNotice = id => request.delete(`/admin/notices/${id}`)

export const pageSalaries = params => request.get('/admin/salaries', { params })
export const mySalaries = () => request.get('/salaries/mine')
export const addSalary = data => request.post('/admin/salaries', data)
export const updateSalary = (id, data) => request.put(`/admin/salaries/${id}`, data)
export const paySalary = id => request.post(`/admin/salaries/${id}/pay`)

export const myLeaves = () => request.get('/leaves/mine')
export const submitLeave = data => request.post('/leaves', data)
export const pageLeaves = params => request.get('/admin/leaves', { params })
export const approveLeave = (id, data) => request.put(`/admin/leaves/${id}/approve`, data)

export const myTransfers = () => request.get('/transfers/mine')
export const submitTransfer = data => request.post('/transfers', data)
export const pageTransfers = params => request.get('/admin/transfers', { params })
export const approveTransfer = (id, data) => request.put(`/admin/transfers/${id}/approve`, data)
